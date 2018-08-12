package com.zdy.project.wechat_chatroom_helper.plugins.message

import android.database.Cursor
import com.zdy.project.wechat_chatroom_helper.ChatInfoModel
import de.robv.android.xposed.XposedHelpers

object MessageFactory {

    private const val SqlForGetAllOfficial = "select unReadCount, status, isSend, conversationTime," +
            "rconversation.username, rcontact.nickname, content, msgType ,digest, digestUser, attrflag, editingMsg, " +
            "atCount, unReadMuteCount, UnReadInvite from rconversation, rcontact " +
            "where ( rcontact.username = rconversation.username and rcontact.verifyFlag = 24) and ( parentRef is null  or parentRef = '' )  " +
            "and ( 1 !=1 or rconversation.username like '%@chatroom' or rconversation.username like '%@openim' or rconversation.username not like '%@%' )  " +
            "and rconversation.username != 'qmessage' order by flag desc"

    private const val SqlForGetAllChatroom = "select unReadCount, status, isSend, conversationTime, " +
            "rconversation.username, rcontact.nickname, content, msgType, digest, digestUser, attrflag, editingMsg, " +
            "atCount, unReadMuteCount, UnReadInvite from rconversation, rcontact " +
            "where  rcontact.username = rconversation.username and  rconversation.username like '%@chatroom' order by flag desc"

    private fun SqlForByUsername(field_username: String) = "select unReadCount, status, isSend, " +
            "conversationTime, rconversation.username, rcontact.nickname, content, msgType, digest," +
            "digestUser, attrflag, editingMsg, atCount, unReadMuteCount, UnReadInvite " +
            "from rconversation, rcontact " +
            "where rconversation.username = rcontact.username and rconversation.username = '$field_username'"


    @JvmStatic
    fun getDataBaseFactory(any: Any) = XposedHelpers.findField(any::class.java, "mCursorFactory").apply { isAccessible = true }.get(any)


    fun getAllChatroom(): ArrayList<ChatInfoModel> {

        val cursor = XposedHelpers.callMethod(MessageHandler.MessageDatabaseObject, "rawQuery", SqlForGetAllChatroom, null) as Cursor

        val list = arrayListOf<ChatInfoModel>()

        while (cursor.moveToNext()) {
            list.add(buildChatInfoModelByCursor(cursor))
        }
        return list
    }

    fun getSingle(field_username: String) =
            buildChatInfoModelByCursor((XposedHelpers.callMethod(MessageHandler.MessageDatabaseObject, "rawQueryWithFactory",
                    getDataBaseFactory(MessageHandler.MessageDatabaseObject!!), SqlForByUsername(field_username), null, null) as Cursor).apply { moveToNext() })


    fun getAllOfficial(): ArrayList<ChatInfoModel> {
        val cursor = XposedHelpers.callMethod(MessageHandler.MessageDatabaseObject, "rawQuery", SqlForGetAllOfficial, null) as Cursor

        val list = arrayListOf<ChatInfoModel>()

        while (cursor.moveToNext()) {
            list.add(buildChatInfoModelByCursor(cursor))
        }
        return list
    }

    private fun buildChatInfoModelByCursor(cursor: Cursor): ChatInfoModel {
        return ChatInfoModel().apply {
            username = cursor.getString(cursor.getColumnIndex("username"))
            nickname = cursor.getString(cursor.getColumnIndex("nickname"))
            content = cursor.getString(cursor.getColumnIndex("content"))
            digest = cursor.getString(cursor.getColumnIndex("digest"))
            digestUser = cursor.getString(cursor.getColumnIndex("digestUser"))
            editingMsg = cursor.getString(cursor.getColumnIndex("editingMsg"))
            msgType = cursor.getString(cursor.getColumnIndex("msgType"))
            conversationTime = cursor.getLong(cursor.getColumnIndex("conversationTime"))
            isSend = cursor.getInt(cursor.getColumnIndex("isSend"))
            status = cursor.getInt(cursor.getColumnIndex("status"))
            attrflag = cursor.getInt(cursor.getColumnIndex("attrflag"))
            atCount = cursor.getInt(cursor.getColumnIndex("atCount"))
            unReadMuteCount = cursor.getInt(cursor.getColumnIndex("unReadMuteCount"))
            UnReadInvite = cursor.getInt(cursor.getColumnIndex("UnReadInvite"))
            unReadCount = cursor.getInt(cursor.getColumnIndex("unReadCount"))
        }
    }

}