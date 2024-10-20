package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.Maps;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class NBTTagCompound extends NBTBase {
    private final Map<String, NBTBase> tagMap = Maps.newHashMap();

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        for (final String s : this.tagMap.keySet()) {
            final NBTBase nbtbase = this.tagMap.get(s);
            writeEntry(s, nbtbase, output);
        }

        output.writeByte(0);
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(384L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte b0;

            while ((b0 = readType(input, sizeTracker)) != 0) {
                final String s = readKey(input, sizeTracker);
                sizeTracker.read(224 + 16 * s.length());
                final NBTBase nbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);

                if (this.tagMap.put(s, nbtbase) != null) {
                    sizeTracker.read(288L);
                }
            }
        }
    }

    public Set<String> getKeySet() {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public void setTag(final String key, final NBTBase value) {
        this.tagMap.put(key, value);
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public void setByte(final String key, final byte value) {
        this.tagMap.put(key, new NBTTagByte(value));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public void setShort(final String key, final short value) {
        this.tagMap.put(key, new NBTTagShort(value));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public void setInteger(final String key, final int value) {
        this.tagMap.put(key, new NBTTagInt(value));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public void setLong(final String key, final long value) {
        this.tagMap.put(key, new NBTTagLong(value));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public void setFloat(final String key, final float value) {
        this.tagMap.put(key, new NBTTagFloat(value));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public void setDouble(final String key, final double value) {
        this.tagMap.put(key, new NBTTagDouble(value));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public void setString(final String key, final String value) {
        this.tagMap.put(key, new NBTTagString(value));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public void setByteArray(final String key, final byte[] value) {
        this.tagMap.put(key, new NBTTagByteArray(value));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public void setIntArray(final String key, final int[] value) {
        this.tagMap.put(key, new NBTTagIntArray(value));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public void setBoolean(final String key, final boolean value) {
        this.setByte(key, (byte) (value ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(final String key) {
        return this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(final String key) {
        final NBTBase nbtbase = this.tagMap.get(key);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(final String key) {
        return this.tagMap.containsKey(key);
    }

    public boolean hasKey(final String key, final int type) {
        final int i = this.getTagId(key);

        if (i == type) {
            return true;
        } else if (type != 99) {
            if (i > 0) {
            }

            return false;
        } else {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getByte();
        } catch (final ClassCastException var3) {
            return (byte) 0;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getShort();
        } catch (final ClassCastException var3) {
            return (short) 0;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getInt();
        } catch (final ClassCastException var3) {
            return 0;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getLong();
        } catch (final ClassCastException var3) {
            return 0L;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getFloat();
        } catch (final ClassCastException var3) {
            return 0.0F;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(final String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getDouble();
        } catch (final ClassCastException var3) {
            return 0.0D;
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(final String key) {
        try {
            return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
        } catch (final ClassCastException var3) {
            return "";
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(final String key) {
        try {
            return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(key)).getByteArray();
        } catch (final ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 7, classcastexception));
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(final String key) {
        try {
            return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(key)).getIntArray();
        } catch (final ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 11, classcastexception));
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(final String key) {
        try {
            return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound) this.tagMap.get(key);
        } catch (final ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 10, classcastexception));
        }
    }

    /**
     * Gets the NBTTagList object with the given name. Args: name, NBTBase type
     */
    public NBTTagList getTagList(final String key, final int type) {
        try {
            if (this.getTagId(key) != 9) {
                return new NBTTagList();
            } else {
                final NBTTagList nbttaglist = (NBTTagList) this.tagMap.get(key);
                return nbttaglist.tagCount() > 0 && nbttaglist.getTagType() != type ? new NBTTagList() : nbttaglist;
            }
        } catch (final ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 9, classcastexception));
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(final String key) {
        return this.getByte(key) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void removeTag(final String key) {
        this.tagMap.remove(key);
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        for (final Entry<String, NBTBase> entry : this.tagMap.entrySet()) {
            if (stringbuilder.length() != 1) {
                stringbuilder.append(',');
            }

            stringbuilder.append(entry.getKey()).append(':').append(entry.getValue());
        }

        return stringbuilder.append('}').toString();
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */
    private CrashReport createCrashReport(final String key, final int expectedType, final ClassCastException ex) {
        final CrashReport crashreport = CrashReport.makeCrashReport(ex, "Reading NBT data");
        final CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
        crashreportcategory.addCrashSectionCallable("Tag type found", new Callable<String>() {
            public String call() throws Exception {
                return NBTBase.NBT_TYPES[NBTTagCompound.this.tagMap.get(key).getId()];
            }
        });
        crashreportcategory.addCrashSectionCallable("Tag type expected", new Callable<String>() {
            public String call() throws Exception {
                return NBTBase.NBT_TYPES[expectedType];
            }
        });
        crashreportcategory.addCrashSection("Tag name", key);
        return crashreport;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (final String s : this.tagMap.keySet()) {
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(final Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            final NBTTagCompound nbttagcompound = (NBTTagCompound) p_equals_1_;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    private static void writeEntry(final String name, final NBTBase data, final DataOutput output) throws IOException {
        output.writeByte(data.getId());

        if (data.getId() != 0) {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(final DataInput input, final NBTSizeTracker sizeTracker) throws IOException {
        return input.readByte();
    }

    private static String readKey(final DataInput input, final NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }

    static NBTBase readNBT(final byte id, final String key, final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        final NBTBase nbtbase = NBTBase.createNewByType(id);

        try {
            nbtbase.read(input, depth, sizeTracker);
            return nbtbase;
        } catch (final IOException ioexception) {
            final CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
            crashreportcategory.addCrashSection("Tag name", key);
            crashreportcategory.addCrashSection("Tag type", Byte.valueOf(id));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
     * types of tags are overwritten from the given compound.
     */
    public void merge(final NBTTagCompound other) {
        for (final String s : other.tagMap.keySet()) {
            final NBTBase nbtbase = other.tagMap.get(s);

            if (nbtbase.getId() == 10) {
                if (this.hasKey(s, 10)) {
                    final NBTTagCompound nbttagcompound = this.getCompoundTag(s);
                    nbttagcompound.merge((NBTTagCompound) nbtbase);
                } else {
                    this.setTag(s, nbtbase.copy());
                }
            } else {
                this.setTag(s, nbtbase.copy());
            }
        }
    }
}
