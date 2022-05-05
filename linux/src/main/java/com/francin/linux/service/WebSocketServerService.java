package com.francin.linux.service;

import javax.websocket.Session;
import java.io.IOException;

public interface WebSocketServerService {

    String onOpen(Session session, String type, String id) throws IOException;

    void onClose();

    void onMessage(String message) throws Exception;

    void sendAllMessage(String type, String message);

    String checkOnline(String id);

    String sendOneMessage(String type, String id, String message);

}
