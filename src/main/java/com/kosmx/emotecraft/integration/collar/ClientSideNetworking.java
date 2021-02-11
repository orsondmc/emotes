package com.kosmx.emotecraft.integration.collar;

import com.kosmx.emotecraft.mixinInterface.EmotePlayerInterface;
import com.kosmx.emotecraft.network.ClientNetwork;
import com.kosmx.emotecraftCommon.EmoteData;
import com.kosmx.emotecraftCommon.network.EmotePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

/**
 * Proxy class, it shouldn't be loaded if collar is not installed
 */
public class ClientSideNetworking {

    /**
     * Emotecraft networking version
     * {@link com.kosmx.emotecraftCommon.CommonData#networkingVersion}
     */
    public static int emotecraftVersionOnTheOtherSide;

    //Emotecraft will call this method, when the client play an emote
    public static void onClientSendEmotePlay(PlayerEntity player, EmoteData emoteData, boolean isRepeating){
        ByteBuf buf = Unpooled.buffer();
        EmotePacket packet = new EmotePacket(emoteData, new UUID(0, 0)); //I'll remove UUID if it will work.
        packet.isRepeat = isRepeating; //Client will resend long emotes, meaning that it is still playing that emote.//and for players, who wasn't there when the player started playing that emote
        packet.write(buf, emotecraftVersionOnTheOtherSide);

        //If you need to send the emotes to multiple people with different versions use packet.write multiple times
        /*
        for(PlayerEntity otherPlayer:everyone){
            ByteBuf otherBuf = Unpooled.buffer();
            packet.write(otherBuf, otherPlayersEmotecraftVersion);
            collar.send(otherBuf);
        }
         */

        buf.array(); //The byte array from the buffer. idk how do you want to send it.

        Collar.send(buf); // idk what will be here...

    }

    //Collar should call this method after receiving an Emote.
    public static void onCollarReceiveEmote(PlayerEntity player, byte[] bytes){

        ByteBuf buf = Unpooled.wrappedBuffer(bytes);//You can give me ByteBuf or PacketByteBuf.
        //Unpooled.wrappedBuffer will NOT copy the byte array, if you need that, use Unpooled.copiedBuffer(bytes).
        //PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(bytes));
        EmotePacket packet = new EmotePacket();
        packet.read(buf, 0); //validation is an anti-cheat feature, It will return a false value, if invalid, but it will read it always.
        EmoteData emoteData = packet.getEmote();

        //Collar should act as a proxy, not as an emotePlay manager
        ClientNetwork.clientReceiveEmote(emoteData, packet.isRepeat, player); //the method, called after an emote receive

        //if you want to directly start an emote do
        //((EmotePlayerInterface)player).playEmote(emoteData);
        //Mixin will make this casting possible, but the compiler doesn't know that, I need to cast manually.
    }

    public static void onClientStop(PlayerEntity player){
        //StopPacket only contains the UUID, sending it is unnecessary.
        //you can send it differently.
        //I may merge the packet names into one.
    }

    public static void onCollarReceiveStop(PlayerEntity player){

        ClientNetwork.clientReceiveStop((EmotePlayerInterface) player);
    }

}
