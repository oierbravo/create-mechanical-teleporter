package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical.TeleporterRendererHelper;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportersNetwork;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.LockNetworkPayload;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToGlobalPosPayload;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToTrainPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModGuiTextures;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.WeakReference;
import java.util.*;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class TeleporterManagerScreen extends AbstractSimiContainerScreen<TeleporterManagerMenu> {
    private boolean isAdmin;
    private boolean isLocked;
    private boolean scrollHandleActive;
    public LerpedFloat entryScroll;
    final int rowWidth = 161;
    final int rowHeight = 10;
    final int maxRows = 5;
    int entriesX;
    int entriesY;
    TeleporterManagerBlockEntity blockEntity;
    TeleportersNetwork network;
    int lockX;
    int lockY;
    int windowWidth;
    int windowHeight;
    private List<Rect2i> extraAreas = Collections.emptyList();
    protected ModGuiTextures background;

    private IconButton confirmButton;
    private IconButton goButton;

    int currentSelectedIndex;
    final int noneHovered = -1;

    List<ITeleporterManagerEntry> allLinks;
    List<TeleporterBehavior> displayedLinks;
    List<TeleporterBehavior> trainLinks;

    GlobalPos globalPos;

    WeakReference<TeleporterBehavior> teleporter;

    public TeleporterManagerScreen(TeleporterManagerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        background = ModGuiTextures.TELEPORTER_MANAGER;
        isAdmin = menu.isAdmin;
        isLocked = menu.isLocked;
        blockEntity = container.contentHolder;
        entryScroll = LerpedFloat.linear()
                .startWithValue(0);

        network = container.network;
        allLinks = new ArrayList<>();
        displayedLinks = new ArrayList<>();
        trainLinks = new ArrayList<>();
        globalPos = new GlobalPos(blockEntity.getLevel().dimension(),blockEntity.getBlockPos());


        // Find the teleporter for rendering
        for (int yOffset : Iterate.zeroAndOne) {
            for (Direction side : Iterate.horizontalDirections) {
                BlockPos teleporterPos = blockEntity.getBlockPos()
                        .below(yOffset)
                        .relative(side,2);
                BlockEntity be = blockEntity.getLevel().getBlockEntity(teleporterPos);
                if(be == null)
                    continue;

                TeleporterBehavior teleporterBehavior = BlockEntityBehaviour.get(be, TeleporterBehavior.TYPE);
                if(teleporterBehavior == null)
                    continue;

                teleporter = new WeakReference<>(teleporterBehavior);
                return;

            }
        }
    }

    @Override
    protected void init() {
        setWindowSize(windowWidth = background.getWidth(),windowHeight = background.getHeight());
        super.init();
        clearWidgets();

        confirmButton = new IconButton(leftPos + background.getWidth() - 34, topPos + background.getHeight()/2 - 20, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        confirmButton.setToolTip(Component.literal("Exit"));
        addRenderableWidget(confirmButton);


        goButton = new IconButton(leftPos + background.getWidth() - 64, topPos + background.getHeight()/2 - 20, AllIcons.I_TARGET);
        goButton.withCallback(() -> this.sendGo());
        goButton.setToolTip(Component.literal("GO"));
        addRenderableWidget(goButton);


        int x = getGuiLeft();
        int y = getGuiTop();
        lockX = x + background.getWidth() - 100;
        lockY = y + background.getHeight()/2 - 18;

        entriesX = x + 23;
        entriesY = y - background.getHeight()/2 + 28;

        currentSelectedIndex = -1;

    }

    private void sendGo() {
        if(currentSelectedIndex == noneHovered)
            return;
        minecraft.player.closeContainer();
        if(teleporter == null)
            return;
        this.allLinks.get(currentSelectedIndex).teleportTo();
        //GlobalPos globalPos = this.allLinks.get(currentSelectedIndex).getGlobalPos();
        //ModMessages.sendToServer(new RequestTeleportToGlobalPosPayload(globalPos));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        float currentScroll = entryScroll.getValue(partialTicks);

        background.render(guiGraphics, x, y - 55);
        int hoveredEntry = getHoveredEntry(mouseX, mouseY);

        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth() + 40);
        int invY = topPos + background.getHeight() - 55;
        renderPlayerInventory(guiGraphics, invX, invY);


        Font font = Minecraft.getInstance().font;
        //int startIndex = 0;
        int startIndex = (int) entryScroll.getValue();
        int lastDisplayedIndex = Math.min(startIndex + maxRows, allLinks.size() -1);

        //allLinks =  TeleporterBehavior.getAllPresent(network.id,false,false).stream().toList();
        allLinks = gatherEntries();

        for (int index = startIndex; index >= 0 && index <= lastDisplayedIndex; index++) {
            ITeleporterManagerEntry entry = allLinks.get(index);
            //guiGraphics.drawString(font, link.getPos().toShortString() + " " + address, teleportersX, teleportersY + 10 * index, Color.WHITE.getRGB());
            int localIndex = index - startIndex;
            if(entry instanceof TrainEntry){
                int a = 0;
            }
            drawEntry(guiGraphics, entriesX, entriesY + 10 * localIndex, entry, localIndex == hoveredEntry, index == currentSelectedIndex);
        }

        // Render lock option
        if (isAdmin)
            (isLocked ? AllGuiTextures.STOCK_KEEPER_REQUEST_LOCKED : AllGuiTextures.STOCK_KEEPER_REQUEST_UNLOCKED)
                    .render(guiGraphics, lockX, lockY);


        PoseStack ms = guiGraphics.pose();
        // Scroll bar
        int windowH = 55;//windowHeight - 92;
        int totalH = getMaxScroll() * rowHeight + windowH;
        int barSize = Math.max(5, Mth.floor((float) windowH / totalH * (windowH - 2)));
        if (barSize < windowH - 2) {
            int barX = entriesX + 161;
            int barY = topPos + background.getHeight()/2 - 89;
            ms.pushPose();
            ms.translate(0, (currentScroll * rowHeight) / totalH * (windowH - 2), 0);
            AllGuiTextures pad = AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_PAD;
            guiGraphics.blit(pad.location, barX, barY, pad.getWidth(), barSize, pad.getStartX(), pad.getStartY(),
                    pad.getWidth(), pad.getHeight(), 256, 256);
            AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_TOP.render(guiGraphics, barX, barY);
            if (barSize > 16)
                AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_MID.render(guiGraphics, barX, barY + barSize / 2 - 4);
            AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_BOT.render(guiGraphics, barX, barY + barSize - 5);
            ms.popPose();
        }
        if(teleporter == null)
            return;
        TeleporterBehavior teleporterBehavior = teleporter.get();
        if (teleporterBehavior != null && teleporterBehavior.blockEntity != null && !teleporterBehavior.blockEntity.isRemoved()) {
            ms.pushPose();
            int entityX = x - 35;
            int entityY = y + windowHeight - 43;
            ms.translate(entityX, entityY, -0);
            ms.mulPose(Axis.XP.rotationDegrees(-22.5f));
            ms.mulPose(Axis.YP.rotationDegrees(-45));
            ms.scale(48, -48, 48);
            PartialModel drawHat = AllPartialModels.LOGISTICS_HAT;
            Lighting.setupForEntityInInventory();

            VertexConsumer translucent = guiGraphics.bufferSource().getBuffer(RenderType.translucent());
            CachedBuffers.block(teleporterBehavior.blockEntity.getBlockState()).renderInto(ms,translucent);

            TeleporterRendererHelper.renderCoreShared(ms, null, guiGraphics.bufferSource(), minecraft.level,
                    teleporterBehavior.blockEntity.getBlockState(), true, -1);
            Lighting.setupFor3DItems();
            ms.popPose();
        }

    }
    private void clampScrollBar() {
        int maxScroll = getMaxScroll();
        float prevTarget = entryScroll.getChaseTarget();
        float newTarget = Mth.clamp(prevTarget, 0, maxScroll);
        if (prevTarget != newTarget)
            entryScroll.startWithValue(newTarget);
    }

    private int getMaxScroll() {
        int totalRows = allLinks.size();
        int maxScroll = Math.max(totalRows - maxRows, 0);
        return maxScroll;
    }
    private void drawEntry(GuiGraphics guiGraphics, int x, int y, ITeleporterManagerEntry entry, boolean isHovered, boolean isSelected){
        Font font = Minecraft.getInstance().font;

        int entryBgX = x - 1;
        int entryBgY = y - 1;
        if(isHovered)
            ModGuiTextures.TELEPORTER_MANAGER_HOVER.render(guiGraphics,entryBgX ,entryBgY);
        else if(isSelected)
            ModGuiTextures.TELEPORTER_MANAGER_SELECTED.render(guiGraphics,entryBgX ,entryBgY);
        else
            ModGuiTextures.TELEPORTER_MANAGER_ENTRY.render(guiGraphics,entryBgX ,entryBgY);
        Component label = entry.getLabel();
        guiGraphics.drawString(font, entry.getLabel(), x, y, Color.WHITE.getRGB());


        /*if(entry.type instanceof TeleporterManagerBlockEntity){
            guiGraphics.drawString(font, "Manager", x, y, Color.WHITE.getRGB());
            return;
        }
        String address = (Objects.equals(teleporterBehavior.signBasedAddress, "")) ? "Empty address": teleporterBehavior.signBasedAddress;
        guiGraphics.drawString(font, "Teleporter: " +  address, x, y, Color.WHITE.getRGB());*/



    }

    @Override
    protected void containerTick() {
        super.containerTick();


        entryScroll.tickChaser();

        if (Math.abs(entryScroll.getValue() - entryScroll.getChaseTarget()) < 1 / 16f)
            entryScroll.setValue(entryScroll.getChaseTarget());

    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        int hoveredEntry = getHoveredEntry(mouseX, mouseY);
        int currentHoveredIndex = (hoveredEntry != noneHovered) ? hoveredEntry + (int) entryScroll.getValue() : noneHovered;

        if (hoveredEntry != noneHovered && currentHoveredIndex != noneHovered) {
            ITeleporterManagerEntry entry = allLinks.get(currentHoveredIndex);
            List<FormattedCharSequence> lines = entry.getTooltips().stream().map(Component::getVisualOrderText).toList();
            graphics.renderTooltip(font,lines, mouseX, mouseY);
        }
        if(currentSelectedIndex != noneHovered){
            goButton.active = true;
        } else {
            goButton.active = false;
        }

        // Render tooltip of lock option
        if ( isAdmin && mouseX > lockX && mouseX <= lockX + 15 && mouseY > lockY
                && mouseY <= lockY + 15) {
            graphics.renderComponentTooltip(font,
                    List.of(
                            CreateLang.translate(isLocked ? "gui.stock_keeper.network_locked" : "gui.stock_keeper.network_open")
                                    .component(),
                            CreateLang.translate("gui.stock_keeper.network_lock_tip")
                                    .style(ChatFormatting.GRAY)
                                    .component(),
                            CreateLang.translate("gui.stock_keeper.network_lock_tip_1")
                                    .style(ChatFormatting.GRAY)
                                    .component(),
                            CreateLang.translate("gui.stock_keeper.network_lock_tip_2")
                                    .style(ChatFormatting.DARK_GRAY)
                                    .style(ChatFormatting.ITALIC)
                                    .component()),
                    mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        super.mouseClicked(pMouseX, pMouseY, pButton);
        boolean lmb = pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        boolean rmb = pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT;

        int hoveredEntry = getHoveredEntry((int) pMouseX,(int)  pMouseY);
        this.currentSelectedIndex = hoveredEntry + (int) entryScroll.getValue();
        // Scroll bar
        int barX = entriesX  + rowWidth + 20;
        if (getMaxScroll() > 0 && lmb && pMouseX > barX && pMouseX <= barX + 8 && pMouseY > getGuiTop() + 15
                && pMouseY < getGuiTop() + windowHeight - 82) {
            scrollHandleActive = true;
            if (minecraft.isWindowActive())
                GLFW.glfwSetInputMode(minecraft.getWindow()
                        .getWindow(), 208897, GLFW.GLFW_CURSOR_HIDDEN);
            return true;
        }

        // Lock
        if (isAdmin && entryScroll.getChaseTarget() == 0 && lmb && pMouseX > lockX && pMouseX <= lockX + 15
                && pMouseY > lockY && pMouseY <= lockY + 15) {
            isLocked = !isLocked;
            //CatnipServices.NETWORK.sendToServer(new StockKeeperLockPacket(blockEntity.getBlockPos(), isLocked));
            ModMessages.sendToServer(new LockNetworkPayload(this.network.id, isLocked));
            playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            return true;
        }
        return true;
    }
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && scrollHandleActive) {
            scrollHandleActive = false;
            if (minecraft.isWindowActive())
                GLFW.glfwSetInputMode(minecraft.getWindow()
                        .getWindow(), 208897, GLFW.GLFW_CURSOR_NORMAL);
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        int hoveredSlot = getHoveredEntry((int) mouseX, (int) mouseY);
        boolean noHover = hoveredSlot == noneHovered;

        if (noHover || !hasShiftDown() && getMaxScroll() != 0) {
            int maxScroll = getMaxScroll();
            int direction = (int) (Math.ceil(Math.abs(scrollY)) * -Math.signum(scrollY));
            float newTarget = Mth.clamp(Math.round(entryScroll.getChaseTarget() + direction), 0, maxScroll);
            entryScroll.chase(newTarget, 0.5, LerpedFloat.Chaser.EXP);
            return true;
        }
        return true;
    }
    private int getHoveredEntry(int x, int y) {
        x += 1;
        if (x < entriesX || x >= entriesX + rowWidth)
            return noneHovered;


        if (y < entriesY || y > entriesY + rowHeight * maxRows)
            return noneHovered;
        if (!entryScroll.settled())
            return noneHovered;

        int localY = y - entriesY;
        for(int i = 0; i < maxRows; i++){
          if(localY >= i * rowHeight && localY < rowHeight * i + rowHeight)
              return i;
        }
        return noneHovered;
    }
    private boolean isWidgetHovered(AbstractSimiWidget widget, int mouseX, int mouseY){
        int confirmX = widget.getX();
        int confirmY = widget.getY();
        int confirmW = widget.getWidth();
        int confirmH = widget.getHeight();

        if (mouseX < confirmX || mouseX >= confirmX + confirmW)
            return false;
        if (mouseY < confirmY || mouseY >= confirmY + confirmH)
            return false;
        return true;
    }
    private boolean isConfirmHovered(int mouseX, int mouseY) {
        int confirmX = confirmButton.getX();
        int confirmY = confirmButton.getY();
        int confirmW = confirmButton.getWidth();
        int confirmH = confirmButton.getHeight();

        if (mouseX < confirmX || mouseX >= confirmX + confirmW)
            return false;
        if (mouseY < confirmY || mouseY >= confirmY + confirmH)
            return false;
        return true;
    }
    private List<ITeleporterManagerEntry> gatherEntries(){
        List<ITeleporterManagerEntry> entryList = new ArrayList<>();


        List<TeleporterBehavior> links =  TeleporterBehavior.getAllPresent(network.id,false,false).stream().toList();



        for( TeleporterBehavior teleporterBehavior : links){
            entryList.add(TeleporterEntry.from(teleporterBehavior));
        }
        for( TeleportersNetwork.TrainLink trainLink : this.network.trainLinks){
            entryList.add(TrainEntry.from(trainLink));
        }


        return entryList;
    }

    private enum EntryType {
        TELEPORTER, MANAGER, TRAIN
    }
    private interface ITeleporterManagerEntry {
        public Component getLabel();
        public List<Component> getTooltips();
        public void teleportTo();
    }
    private static class TeleporterEntry implements ITeleporterManagerEntry{
        private TeleporterBehavior teleporterBehavior;
        private TeleporterEntry(TeleporterBehavior teleporterBehavior){
            this.teleporterBehavior = teleporterBehavior;
        }
        public static TeleporterEntry from(TeleporterBehavior teleporterBehavior){
            return new TeleporterEntry(teleporterBehavior);
        }

        @Override
        public Component getLabel() {
            if(!teleporterBehavior.isTeleportable()){
                return Component.literal("Manager");
            }
            String address = (Objects.equals(teleporterBehavior.signBasedAddress, "")) ? "Empty address": teleporterBehavior.signBasedAddress;
            return Component.literal("Teleporter: " +  address);
        }

        @Override
        public List<Component> getTooltips(){
            return teleporterBehavior.getTooltips();
        }

        @Override
        public void teleportTo(){
            ModMessages.sendToServer(new RequestTeleportToGlobalPosPayload(teleporterBehavior.getGlobalPos()));
        }
    }
    private static class TrainEntry implements ITeleporterManagerEntry{
        private UUID trainId;
        private int carriageId;
        private String address;
        private TrainEntry(UUID trainId, int carriageId, String address){
            this.trainId = trainId;
            this.carriageId = carriageId;
            this.address = address;
        }
        public static TrainEntry from(TeleportersNetwork.TrainLink trainLink){
            return new TrainEntry(trainLink.trainId(), trainLink.carriageId(), trainLink.address());
        }

        @Override
        public List<Component> getTooltips() {
            return List.of(Component.literal("Carriage Id: " + carriageId));
        }

        @Override
        public Component getLabel() {
            return Component.literal("Train: " + address);
        }

        @Override
        public void teleportTo(){
            ModMessages.sendToServer(new RequestTeleportToTrainPayload(trainId, carriageId, address));
        }
    }
}
