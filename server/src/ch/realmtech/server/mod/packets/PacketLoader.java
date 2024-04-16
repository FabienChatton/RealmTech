package ch.realmtech.server.mod.packets;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.packet.Packet;
import ch.realmtech.server.packet.clientPacket.*;
import ch.realmtech.server.packet.serverPacket.*;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import io.netty.buffer.ByteBuf;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PacketLoader extends Entry {
    private List<Map.Entry<Class<? extends Packet>, Function<ByteBuf, ? extends Packet>>> packets;

    public PacketLoader() {
        super("PacketLoader");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        packets = new ArrayList<>();
        packets.add(new SimpleEntry<>(ConnexionJoueurReussitPacket.class, ConnexionJoueurReussitPacket::new));
        packets.add(new SimpleEntry<>(ConnexionJoueurReussitPacket.class, ConnexionJoueurReussitPacket::new));
        packets.add(new SimpleEntry<>(DemandeDeConnexionJoueurPacket.class, DemandeDeConnexionJoueurPacket::new));
        packets.add(new SimpleEntry<>(PlayerMovePacket.class, PlayerMovePacket::new));
        packets.add(new SimpleEntry<>(ChunkAMonterPacket.class, ChunkAMonterPacket::new));
        packets.add(new SimpleEntry<>(ChunkADamnePacket.class, ChunkADamnePacket::new));
        packets.add(new SimpleEntry<>(ChunkAReplacePacket.class, ChunkAReplacePacket::new));
        packets.add(new SimpleEntry<>(DeconnectionJoueurPacket.class, DeconnectionJoueurPacket::new));
        packets.add(new SimpleEntry<>(CellBreakRequestPacket.class, CellBreakRequestPacket::new));
        packets.add(new SimpleEntry<>(CellBreakPacket.class, CellBreakPacket::new));
        packets.add(new SimpleEntry<>(TickBeatPacket.class, TickBeatPacket::new));
        packets.add(new SimpleEntry<>(GetPlayerInventorySessionPacket.class, GetPlayerInventorySessionPacket::new));
        packets.add(new SimpleEntry<>(ItemOnGroundPacket.class, ItemOnGroundPacket::new));
        packets.add(new SimpleEntry<>(ItemOnGroundSupprimerPacket.class, ItemOnGroundSupprimerPacket::new));
        packets.add(new SimpleEntry<>(ConsoleCommandeRequestPacket.class, ConsoleCommandeRequestPacket::new));
        packets.add(new SimpleEntry<>(WriteToConsolePacket.class, WriteToConsolePacket::new));
        packets.add(new SimpleEntry<>(MoveStackToStackPacket.class, MoveStackToStackPacket::new));
        packets.add(new SimpleEntry<>(InventorySetPacket.class, InventorySetPacket::new));
        packets.add(new SimpleEntry<>(InventoryGetPacket.class, InventoryGetPacket::new));
        packets.add(new SimpleEntry<>(ItemToCellPlaceRequestPacket.class, ItemToCellPlaceRequestPacket::new));
        packets.add(new SimpleEntry<>(CellAddPacket.class, CellAddPacket::new));
        packets.add(new SimpleEntry<>(DisconnectMessagePacket.class, DisconnectMessagePacket::new));
        packets.add(new SimpleEntry<>(TimeGetRequestPacket.class, TimeGetRequestPacket::new));
        packets.add(new SimpleEntry<>(TimeSetPacket.class, TimeSetPacket::new));
        packets.add(new SimpleEntry<>(PhysicEntitySetPacket.class, PhysicEntitySetPacket::new));
        packets.add(new SimpleEntry<>(PlayerSyncPacket.class, PlayerSyncPacket::new));
        packets.add(new SimpleEntry<>(PlayerCreateConnexion.class, PlayerCreateConnexion::new));
        packets.add(new SimpleEntry<>(FurnaceExtraInfoPacket.class, FurnaceExtraInfoPacket::new));
        packets.add(new SimpleEntry<>(RotateFaceCellRequestPacket.class, RotateFaceCellRequestPacket::new));
        packets.add(new SimpleEntry<>(CellSetPacket.class, CellSetPacket::new));
        packets.add(new SimpleEntry<>(EnergyBatterySetEnergyPacket.class, EnergyBatterySetEnergyPacket::new));
        packets.add(new SimpleEntry<>(EnergyGeneratorInfoPacket.class, EnergyGeneratorInfoPacket::new));
        packets.add(new SimpleEntry<>(PlayerPickUpItem.class, PlayerPickUpItem::new));
        packets.add(new SimpleEntry<>(PlayerOutOfRange.class, PlayerOutOfRange::new));
        packets.add(new SimpleEntry<>(SubscribeToEntityPacket.class, SubscribeToEntityPacket::new));
        packets.add(new SimpleEntry<>(UnSubscribeToEntityPacket.class, UnSubscribeToEntityPacket::new));
        packets.add(new SimpleEntry<>(MobDeletePacket.class, MobDeletePacket::new));
        packets.add(new SimpleEntry<>(PlayerWeaponShotPacket.class, PlayerWeaponShotPacket::new));
        packets.add(new SimpleEntry<>(ParticleAddPacket.class, ParticleAddPacket::new));
        packets.add(new SimpleEntry<>(MobAttackCoolDownPacket.class, MobAttackCoolDownPacket::new));
        packets.add(new SimpleEntry<>(PlayerHasWeaponShotPacket.class, PlayerHasWeaponShotPacket::new));
        packets.add(new SimpleEntry<>(EnemyHitPacket.class, EnemyHitPacket::new));
        packets.add(new SimpleEntry<>(QuestCheckboxCompletedPacket.class, QuestCheckboxCompletedPacket::new));
        packets.add(new SimpleEntry<>(QuestSetCompleted.class, QuestSetCompleted::new));
    }

    @Override
    public void postEvaluate() {
        getPackets().forEach((packet) -> ServerContext.PACKETS.put(packet.getKey(), packet.getValue()));
    }

    public List<Map.Entry<Class<? extends Packet>, Function<ByteBuf, ? extends Packet>>> getPackets() {
        return Collections.unmodifiableList(packets);
    }

    public void addPacket(Map.Entry<Class<? extends Packet>, Function<ByteBuf, ? extends Packet>> packet) {
        packets.add(packet);
    }
}
