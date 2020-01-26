package com.etheller.warsmash.viewer5.handlers.tga;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils {

	/**
	 * Takes an images as input and generates an array containing this image and all
	 * possible mipmaps
	 *
	 * @param input
	 * @return
	 */
	public static BufferedImage[] generateMipMaps(final BufferedImage input) {
		int num = 0;
		int curWidth = input.getWidth();
		int curHeight = input.getHeight();
		int pow;
		do {
			num++;
			pow = (int) Math.pow(2.0D, num - 1);
		}
		while ((pow < curWidth) || (pow < curHeight));
		final BufferedImage[] result = new BufferedImage[num];
		result[0] = input;
		for (int i = 1; i < num; i++) {
			curWidth /= 2;
			curHeight /= 2;
			if (curHeight == 0) {
				curHeight = 1;
			}
			if (curWidth == 0) {
				curWidth = 1;
			}
			result[i] = ImageUtils.getScaledInstance(result[(i - 1)], curWidth, curHeight,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
		}
		return result;
	}

	/**
	 * Scales an Image
	 *
	 * @param img
	 * @param targetWidth
	 * @param targetHeight
	 * @param hint          Rendering Hint
	 * @param higherQuality
	 * @return
	 */
	public static BufferedImage getScaledInstance(final BufferedImage img, final int targetWidth,
			final int targetHeight, final Object hint, final boolean higherQuality) {
		final int type = img.getTransparency() == 1 ? 1 : 2;
		BufferedImage ret = img;
		int h;
		int w;
		if (higherQuality) {
			w = img.getWidth();
			h = img.getHeight();
		}
		else {
			w = targetWidth;
			h = targetHeight;
		}
		do {
			if ((higherQuality) && (w > targetWidth)) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			if ((higherQuality) && (h > targetHeight)) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp;
			if (img.getColorModel().hasAlpha() == false) {
				tmp = new BufferedImage(w, h, type);
				final Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();
			}
			else {
				// Necessary because otherwise Bilinear resize would couse transparent pixel to
				// change color
				tmp = resizeWorkAround(ret, w, h, hint);
			}

			ret = tmp;
		}
		while ((w != targetWidth) || (h != targetHeight));
		return ret;
	}

	private static BufferedImage resizeWorkAround(final BufferedImage ret, final int w, final int h,
			final Object hint) {

		final BufferedImage noAlpha = new BufferedImage(ret.getWidth(), ret.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				int color = ret.getRGB(x, y);
				color = color | 0xff000000;
				noAlpha.setRGB(x, y, color);
			}
		}

		final BufferedImage noAlphaSmall = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = noAlphaSmall.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
		g2.drawImage(noAlpha, 0, 0, w, h, null);
		g2.dispose();

		final BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g2 = tmp.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
		g2.drawImage(ret, 0, 0, w, h, null);
		g2.dispose();

		noAlphaSmall.getAlphaRaster().setRect(0, 0, tmp.getAlphaRaster());

		return noAlphaSmall;
	}

	/**
	 * An alternative way to convert an image to type_byte_indexed (paletted) that
	 * avoids dithering.
	 *
	 * @param src
	 * @return
	 */
	public static BufferedImage antiDitherConvert(final BufferedImage src) {
		final BufferedImage convertedImage = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_BYTE_INDEXED);
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				convertedImage.setRGB(x, y, src.getRGB(x, y));
			}
		}
		return convertedImage;
	}

	public static BufferedImage changeImageType(final BufferedImage src, final int type) {
		final BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), type);
		final Graphics2D g = (Graphics2D) img.getGraphics();

		if (img.getColorModel().hasAlpha()) {
			final Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC);
			g.setComposite(comp);
		}

		g.drawImage(src, 0, 0, null);
		g.dispose();

		return img;
	}

	public static BufferedImage convertStandardImageType(final BufferedImage src, final boolean useAlpha) {

		if (useAlpha && (src.getType() == BufferedImage.TYPE_INT_ARGB)) {
			return src;
		}

		if ((useAlpha == false) && (src.getType() == BufferedImage.TYPE_INT_RGB)) {
			return src;
		}

		return ImageUtils.changeImageType(src, useAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

	}
}