package com.kosmx.emotecraft.integration.collar;

import com.kosmx.emotecraftCommon.network.EmotePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * What should collar do on server-side (if no p2p com is happening)
 */
public class ServerSideNetwork {
    public static void StreamEmote(ByteBuf byteBuf, CollarPlayer player){
        EmotePacket emotePacket = new EmotePacket();
        boolean isValid = emotePacket.read(byteBuf, validationThreshold);
        //EmotePacket.read will return false, if the emote is invalid. (but it will do the read anyway)
        if(isValid){
            for(CollarPlayer otherPlayer:everyone){
                if(otherPlayer != player){ //Don't send it back
                    ByteBuf buf = Unpooled.buffer();

                    emotePacket.write(buf, otherPlayer.emotecraftVersion);
                    //write a buf with the other player's emotecraft ver (write will do Math.min(otherPlayersVersion, itsVersion).)
                    otherPlayer.sendPacket(buf); //send it to the player
                }
            }
        }
        else {
            //if you want to validate and the emote is invalid, send a stop packet back to the sender
        }
    }

    public static void StreamStop(){
        //StopPacket only contains the sender as a UUID, you should stream it somehow.
    }
}
