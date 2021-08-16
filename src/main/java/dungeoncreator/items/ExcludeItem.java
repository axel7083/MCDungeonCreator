package dungeoncreator.items;


import dungeoncreator.utils.TileUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ExcludeItem extends Item {

    public ExcludeItem()
    {
        super(new Properties().maxStackSize(1).group(ItemGroup.REDSTONE)
        );
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        String output = TileUtils.setBlockWalkable(playerIn, (byte) 1, 4);

        if(output != null) {
            TranslationTextComponent finalText = new TranslationTextComponent("chat.type.announcement",
                    new StringTextComponent(output));
            playerIn.sendMessage(finalText,playerIn.getUniqueID());
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
