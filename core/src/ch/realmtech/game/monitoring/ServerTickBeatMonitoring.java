package ch.realmtech.game.monitoring;

import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.packet.ServerPacket;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerTickBeatMonitoring extends Thread implements Closeable {
    private final List<Float> tickElapseTimeList;
    private final List<Integer> packetDataReciveSize;
    private final List<Integer> packetDataSendSize;
    private volatile int tickBeatSize;
    private volatile int dataReciveSize;
    private volatile int dataSendSize;
    private volatile boolean run;
    public ServerTickBeatMonitoring() {
        super("ServerTickBeatMonitoring");
        tickElapseTimeList = Collections.synchronizedList(new ArrayList<>());
        packetDataReciveSize = Collections.synchronizedList(new ArrayList<>());
        packetDataSendSize = Collections.synchronizedList(new ArrayList<>());
        tickBeatSize = 0;
        dataReciveSize = 0;
        dataSendSize = 0;
        run = true;

        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        while(run) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            synchronized (tickElapseTimeList) {
                tickBeatSize = tickElapseTimeList.size();
                tickElapseTimeList.clear();
            }
            synchronized (packetDataReciveSize) {
                dataReciveSize = packetDataReciveSize.stream().reduce(Integer::sum).orElse(0);
                packetDataReciveSize.clear();
            }
            synchronized (packetDataSendSize) {
                dataSendSize = packetDataSendSize.stream().reduce(Integer::sum).orElse(0);
                packetDataSendSize.clear();
            }
        }
    }

    public synchronized void addTickElapseTime(float elapseTime) {
        tickElapseTimeList.add(elapseTime);
    }

    public synchronized void addPacketResive(ClientPacket packet) {
        packetDataReciveSize.add(packet.getSize());
    }

    public synchronized void addPacketSend(ServerPacket packet) {
        packetDataSendSize.add(packet.getSize());
    }

    public int getTickBeatPerSeconde() {
        return tickBeatSize;
    }

    public int getPacketDataReciveSizePerSeconde() {
        return dataReciveSize;
    }

    public int getPacketDataSendPerSeconde() {
        return dataSendSize;
    }

    @Override
    public void close() throws IOException {
        run = false;
    }
}
