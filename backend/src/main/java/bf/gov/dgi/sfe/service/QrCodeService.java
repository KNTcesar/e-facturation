package bf.gov.dgi.sfe.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

@Service
// Genere un QR code PNG et verifie qu'il reste scannable.
public class QrCodeService {

    // Construit un QR PNG dimensionne et valide sa lisibilite.
    public byte[] generateScannablePng(String payload, int requestedSize) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Le contenu QR est obligatoire");
        }

        int size = Math.max(128, Math.min(requestedSize, 1024));

        try {
            Map<EncodeHintType, Object> encodeHints = new EnumMap<>(EncodeHintType.class);
            encodeHints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            encodeHints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, size, size, encodeHints);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            byte[] png = output.toByteArray();

            verifyScannable(png, payload);
            return png;
        } catch (WriterException ex) {
            throw new IllegalStateException("Impossible de generer le QR code", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de generation/verification QR", ex);
        }
    }

    // Decode le PNG genere pour confirmer la correspondance exacte du payload.
    private void verifyScannable(byte[] png, String expectedPayload) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
        if (image == null) {
            throw new IllegalStateException("Image QR invalide");
        }

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Map<DecodeHintType, Object> decodeHints = new EnumMap<>(DecodeHintType.class);
        decodeHints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

        Result result = new MultiFormatReader().decode(bitmap, decodeHints);
        if (!expectedPayload.equals(result.getText())) {
            throw new IllegalStateException("Le QR code genere n'est pas conforme au payload attendu");
        }
    }
}
