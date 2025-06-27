package io.lerpmcgerk.mysticalprisms.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.screen.renderer.FluidTankRenderer;
import io.lerpmcgerk.mysticalprisms.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;

public class CrystalGrowerScreen extends AbstractContainerScreen<CrystalGrowerMenu> {

    private static final NumberFormat nf = NumberFormat.getPercentInstance();

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "textures/gui/crystal_grower/crystal_grower_gui.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "textures/gui/progress_bar.png");

    private FluidTankRenderer fluidRenderer;

    public CrystalGrowerScreen(CrystalGrowerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        assignFluidRenderer(4000);
    }

    private void assignFluidRenderer(long capacity)
    {
        fluidRenderer = new FluidTankRenderer(capacity, true, 16, 64);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.blockEntity.getFluid(), 9, 10, fluidRenderer);

            renderTooltipArea(guiGraphics, mouseX, mouseY, x, y, 88, 40, 31, 4,
                    Component.literal(nf.format(menu.getCraftingProgressPercent(1))));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        fluidRenderer.render(guiGraphics, x + 9, y + 10, menu.blockEntity.getFluid());
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE,x + 88, y + 40, 0, 0, menu.getScaledArrowProgress(), 4, 31, 4);
        }
    }

    private void renderTooltipArea(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height, Component text)
    {
        if(MouseUtil.isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, width, height)) {
            guiGraphics.renderTooltip(this.font, List.of(text),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderFluidTooltipArea(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y,
                                        FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if(MouseUtil.isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.renderTooltip(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
