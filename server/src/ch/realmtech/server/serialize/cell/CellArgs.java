package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.registery.CellRegisterEntry;

public record CellArgs(CellRegisterEntry cellRegisterEntry, byte innerChunk) { }
