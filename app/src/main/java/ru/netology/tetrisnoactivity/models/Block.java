package ru.netology.tetrisnoactivity.models;

import android.graphics.Color;
import android.graphics.Point;

import androidx.annotation.NonNull;

import java.util.Random;

import ru.netology.tetrisnoactivity.constants.FieldConstants;

public class Block {

    private int shapeIndex;
    private int frameNumber;
    private BlockColor color;
    private Point position;

    private Block(int shapeIndex, BlockColor blockColor) {
        this.frameNumber = 0;
        this.shapeIndex = shapeIndex;
        this.color = blockColor;
        this.position = new Point(FieldConstants.COLUMN_COUNT.getValue() / 2, 0);
    }

    @NonNull
    public static Block createBlock() {
        Random random = new Random();
        int shapeIndex = random.nextInt(Shape.values().length);
        BlockColor blockColor = BlockColor.values()
                [random.nextInt(BlockColor.values().length)];
        Block block = new Block(shapeIndex, blockColor);
        // Set to the middle
        block.position.x = block.position.x - Shape.values()
                [shapeIndex].getStartPosition();
        return block;
    }

    public enum BlockColor {
        PINK(Color.rgb(255, 105, 180), (byte) 2),
        GREEN(Color.rgb(0, 128, 0), (byte) 3),
        ORANGE(Color.rgb(255, 140, 0), (byte) 4),
        YELLOW(Color.rgb(255, 255, 0), (byte) 5),
        CYAN(Color.rgb(0, 255, 255), (byte) 6);

        BlockColor(int rgbValue, byte value) {
            this.rgbValue = rgbValue;
            this.byteValue = value;
        }

        private final int rgbValue;
        private final int byteValue;
    }

    public static int getColor(byte value) {
        for (BlockColor colour : BlockColor.values()) {
            if (value == colour.byteValue) {
                return colour.rgbValue;
            }
        }
        return -1;
    }

    public final void setState(int frame, Point position) {
        this.frameNumber = frame;
        this.position = position;
    }

    //Не может иметь значение null
    @NonNull
    public final byte[][] getShape(int frameNumber) {
        return Shape.values()[shapeIndex].getFrame(
                frameNumber).as2dByteArray();
    }

    public Point getPosition() {
        return this.position;
    }

    public final int getFrameNumber() {
        return frameNumber;
    }

    public int getColor() {
        return color.rgbValue;
    }

    public byte getStaticValue() {
        return (byte) color.byteValue;
    }

    public final int getFrameCount() {
        return Shape.values()[shapeIndex].getFrameCount();
    }

}
//переменная shapeIndex - включает индекс формы блока
//frameNumber-отслеживает количество фреймов,относящихся к форме блока
//color -цветовые характеристика блока
//position- отслеживание текущей пространственной позиции блока в игровом поле