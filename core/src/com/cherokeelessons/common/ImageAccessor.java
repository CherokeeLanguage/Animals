package com.cherokeelessons.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import aurelienribon.tweenengine.TweenAccessor;

public class ImageAccessor implements TweenAccessor<Image> {

	final public static int BoundingRectangle = 0;// Rectangle
	final public static int X = 1;//
	final public static int Y = 2;//
	final public static int Width = 3;//
	final public static int Height = 4;//
	final public static int OriginX = 5;//
	final public static int OriginY = 6;//
	final public static int Rotation = 7;//
	final public static int ScaleX = 8;//
	final public static int ScaleY = 9;//
	final public static int Color = 10;//
	final public static int ColorRed = 11;
	final public static int ColorGreen = 12;
	final public static int ColorBlue = 13;
	final public static int Alpha = 14;

	@Override
	public int getValues(Image target, int tweenType, float[] returnValues) {
		int c = -1;
		Rectangle rec;
		Color col;
		switch (tweenType) {
		case BoundingRectangle:
			rec = getBoundingRectangle(target);
			returnValues[0]=rec.x;
			returnValues[1]=rec.y;
			returnValues[2]=rec.width;
			returnValues[3]=rec.height;
			c=4;
			break;
		case X:
			returnValues[0]=target.getX();
			c=1;
			break;
		case Y:
			returnValues[0]=target.getY();
			c=1;
			break;
		case Width:
			returnValues[0]=target.getWidth();
			c=1;
			break;
		case Height:
			returnValues[0]=target.getHeight();
			c=1;
			break;
		case OriginX:
			returnValues[0]=target.getOriginX();
			c=1;
			break;
		case OriginY:
			returnValues[0]=target.getOriginY();
			c=1;
			break;
		case Rotation:
			returnValues[0]=target.getRotation();
			c=1;
			break;
		case ScaleX:
			returnValues[0]=target.getScaleX();
			c=1;
			break;
		case ScaleY:
			returnValues[0]=target.getScaleY();
			c=1;
			break;
		case Color:
			col = target.getColor();
			returnValues[0]=col.r;
			returnValues[0]=col.g;
			returnValues[0]=col.b;
			returnValues[0]=col.a;
			c=4;
			break;
		case ColorRed:
			col = target.getColor();
			returnValues[0]=col.r;
			c=1;
			break;
		case ColorGreen:
			col = target.getColor();
			returnValues[0]=col.g;
			c=1;
			break;
		case ColorBlue:
			col = target.getColor();
			returnValues[0]=col.b;
			c=1;
			break;
		case Alpha:
			col = target.getColor();
			returnValues[0]=col.a;
			c=1;
			break;
		default:
		}
		return c;
	}

	private Rectangle getBoundingRectangle(Image target) {
		Rectangle bounds = new Rectangle();
		bounds.x = target.getImageX();
		bounds.y = target.getImageY();
		bounds.width = target.getImageWidth();
		bounds.height = target.getImageHeight();
		return bounds;
	}

	@Override
	public void setValues(Image target, int tweenType, float[] newValues) {
		Rectangle rec;
		float originX;
		float originY;
		com.badlogic.gdx.graphics.Color col;
		switch (tweenType) {
		case BoundingRectangle:
			target.setBounds(newValues[0], newValues[1], newValues[2], newValues[3]);
			break;
		case X:
			target.setX(newValues[0]);
			break;
		case Y:
			target.setY(newValues[0]);
			break;
		case Width:
			rec = getBoundingRectangle(target);
			rec.width=newValues[0];
			target.setBounds(rec.x, rec.y, rec.width, rec.height);
			break;
		case Height:
			rec = getBoundingRectangle(target);
			rec.height=newValues[0];
			target.setBounds(rec.x, rec.y, rec.width, rec.height);
			break;
		case OriginX:
			originX=newValues[0];
			originY=target.getOriginY();
			target.setOrigin(originX, originY);
			break;
		case OriginY:
			originX=target.getOriginX();
			originY=newValues[0];
			target.setOrigin(originX, originY);
			break;
		case Rotation:
			target.setRotation(newValues[0]);
			break;
		case ScaleX:
			target.setScale(newValues[0], target.getScaleY());
			break;
		case ScaleY:
			target.setScale(target.getScaleX(), newValues[0]);
			break;
		case Color:
			target.setColor(newValues[0], newValues[1], newValues[2], newValues[3]);
			break;
		case ColorRed:
			col=target.getColor();
			col.r=newValues[0];
			target.setColor(col);
			break;
		case ColorGreen:
			col=target.getColor();
			col.g=newValues[0];
			target.setColor(col);
			break;
		case ColorBlue:
			col=target.getColor();
			col.b=newValues[0];
			target.setColor(col);
			break;
		case Alpha:
			col=target.getColor();
			col.a=newValues[0];
			target.setColor(col);
			break;
		default:
		}
	}

}
