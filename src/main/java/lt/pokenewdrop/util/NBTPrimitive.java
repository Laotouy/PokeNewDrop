package lt.pokenewdrop.util;

import net.minecraft.server.v1_12_R1.NBTBase;

public abstract class NBTPrimitive extends NBTBase
{
    public abstract long getLong();

    public abstract int getInt();

    public abstract short getShort();

    public abstract byte getByte();

    public abstract double getDouble();

    public abstract float getFloat();
}