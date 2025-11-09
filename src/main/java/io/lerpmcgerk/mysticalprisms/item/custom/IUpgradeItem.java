package io.lerpmcgerk.mysticalprisms.item.custom;

import io.lerpmcgerk.mysticalprisms.block.entity.custom.UpgradableBlockEntity;

public interface IUpgradeItem {
    int getMaxUpgrades();

    void ApplyUpgrade(UpgradableBlockEntity upgradableBlockEntity);
}
