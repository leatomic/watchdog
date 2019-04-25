package io.watchdog.security.web.verification.image;

import io.watchdog.security.web.verification.TokenWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * <p>默认提供的图片验证码图片输出器
 * <p>负责将{@link ImageCode}转化成图片，并通过当前请求对应的{@link HttpServletResponse}的I/O输出到客户端</p>
 */
public class DefaultImageCodeWriter implements TokenWriter<ImageCode> {

    protected final Random random = new Random();

    /**
     * 根据image code生成图片，并将生成的图片写到HttpServletResponse的输出流中
     * @throws IOException
     */
    @Override
    public void write(ImageCode token) throws IOException {

        BufferedImage image = generateImage(token.getKey().toCharArray(), token.getImageWidth(), token.getImageHeight());

        HttpServletResponse destination = getDestination();

        /* 禁止图像缓存 */
        destination.setHeader("Pragma","no-cache");
        destination.setHeader("Cache-Control", "no-cache");
        destination.setDateHeader("Expires", 0);

        destination.setStatus(HttpStatus.OK.value());
        destination.setContentType(MediaType.IMAGE_JPEG_VALUE);
        ImageIO.write(image, "JPEG", destination.getOutputStream());
    }



    private BufferedImage generateImage(char[] seq, int width, int height){

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        Font font = new Font("Comic Sans MS", Font.PLAIN, 20);
        graphics.setColor(getRandomColor(200,250));
        graphics.fillRect(1, 1, width-1, height-1);
        graphics.setFont(font);

        graphics.setColor(new Color(102,102,102));
        graphics.drawRect(0, 0, width-1, height-1);

        int x1,y1,x2,y2;

        /* 画随机线 */
        for (int i = 0; i < 155; i++) {
            x1 = random.nextInt(width - 1);
            y1 = random.nextInt(height - 1);
            x2 = random.nextInt(6) + 1;
            y2 = random.nextInt(12) + 1;
            graphics.setColor(getRandomColor(160,200));
            graphics.drawLine(x1, y1, x1 + x2, y1 + y2);
        }

        /* 从另一方向画随机线 */
        for (int i = 0; i < 70; i++) {
            x1 = random.nextInt(width - 1);
            y1 = random.nextInt(height - 1);
            x2 = random.nextInt(12) + 1;
            y2 = random.nextInt(6) + 1;
            graphics.setColor(getRandomColor(160,200));
            graphics.drawLine(x1, y1, x1 - x2, y1 - y2);
        }

        /* 画随机验证码 */
        for (int i = 0; i < seq.length; i++) {
            graphics.setColor(getRandomColor(20, 110));
            graphics.drawString(String.valueOf(seq[i]), ((width - 16) / seq.length) * i + 10, height - 8);
        }
        graphics.dispose();

        return image;
    }

    private Color getRandomColor(int fc, int bc) {

        if (fc > 255)
            fc = 255;

        if (bc > 255)
            bc = 255;

        int round = bc - fc;
        int r = fc + random.nextInt(round);
        int g = fc + random.nextInt(round);
        int b = fc + random.nextInt(round);

        return new Color(r, g, b);

    }

    private HttpServletResponse getDestination() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getResponse();
    }

}

