package com.uca.ecommerce.services.invoice;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.uca.ecommerce.domain.entities.Address;
import com.uca.ecommerce.domain.entities.Coupon;
import com.uca.ecommerce.domain.entities.Invoice;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.OrderItem;
import com.uca.ecommerce.exceptions.InvoiceGenerationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class PdfInvoiceGenerator {

    private static final Color PAPER = new Color(250, 248, 243);
    private static final Color INK = new Color(38, 34, 27);
    private static final Color CREAM = new Color(236, 230, 218);
    private static final Color MOCHA = new Color(156, 129, 99);
    private static final Color MUTE = new Color(138, 129, 116);
    private static final Color LINE = new Color(226, 219, 207);

    private static final Locale ES = Locale.forLanguageTag("es");
    private static final ZoneId SV_ZONE = ZoneId.of("America/El_Salvador");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy, HH:mm", ES);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm", ES);

    private static final Font BRAND = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, MUTE);
    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, INK);
    private static final Font LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, MUTE);
    private static final Font VALUE_SM = FontFactory.getFont(FontFactory.HELVETICA, 9, INK);
    private static final Font TH = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, CREAM);
    private static final Font TOTAL_LABEL = FontFactory.getFont(FontFactory.HELVETICA, 10, MUTE);
    private static final Font TOTAL_VALUE = FontFactory.getFont(FontFactory.HELVETICA, 10, INK);
    private static final Font GRAND_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INK);
    private static final Font GRAND_VALUE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, MOCHA);
    private static final Font FOOT = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTE);

    public byte[] generate(Invoice invoice, Order order, List<OrderItem> items, String paymentMethod) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 48, 48, 54, 54);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new Watermark());
            document.open();

            document.add(buildHeader(invoice));
            document.add(spacer(18));
            document.add(buildMeta(invoice, order));
            document.add(spacer(14));
            document.add(buildItems(items));
            document.add(spacer(10));
            document.add(buildTotals(invoice, order, paymentMethod));
            document.add(buildFooter(order));

            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new InvoiceGenerationException("Could not generate invoice PDF");
        }
    }

    private PdfPTable buildHeader(Invoice invoice) throws Exception {
        PdfPTable header = new PdfPTable(new float[]{1, 1});
        header.setWidthPercentage(100);

        PdfPCell brand = new PdfPCell();
        brand.setBorder(Rectangle.NO_BORDER);
        brand.setVerticalAlignment(Element.ALIGN_TOP);
        Image logo = logo();
        if (logo != null) {
            logo.scaleToFit(120, 48);
            brand.addElement(logo);
        }
        Paragraph wordmark = new Paragraph("K LAB  ·  MISTER K", BRAND);
        wordmark.setSpacingBefore(4);
        brand.addElement(wordmark);
        header.addCell(brand);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph title = new Paragraph("FACTURA ELECTRONICA", TITLE);
        title.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(title);
        Paragraph num = new Paragraph("No. " + invoice.getControlNumber(), VALUE_SM);
        num.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(num);
        Paragraph date = new Paragraph(local(invoice.getIssuedAt(), DATE) + " (GMT-6)", FontFactory.getFont(FontFactory.HELVETICA, 9, MUTE));
        date.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(date);
        header.addCell(right);

        return header;
    }

    private PdfPTable buildMeta(Invoice invoice, Order order) {
        PdfPTable meta = new PdfPTable(new float[]{1, 1});
        meta.setWidthPercentage(100);
        meta.setSpacingBefore(6);

        meta.addCell(infoBlock("FACTURAR A", List.of(
                invoice.getCustomerName(),
                invoice.getCustomerEmail()
        )));
        meta.addCell(infoBlock("ENTREGAR EN", addressLines(order.getShippingAddress())));

        meta.addCell(infoBlock("PEDIDO", List.of(
                "#" + shortId(order),
                "Fecha " + local(order.getCreatedAt(), DATE_TIME) + " (GMT-6)"
        )));
        String shipName = order.getShippingMethod() != null ? order.getShippingMethod().getName() : "-";
        String shipEta = order.getShippingMethod() != null ? order.getShippingMethod().getEta() : "";
        meta.addCell(infoBlock("ENVIO", List.of(
                shipName,
                shipEta == null ? "" : "Entrega estimada " + shipEta
        )));

        return meta;
    }

    private PdfPCell infoBlock(String label, List<String> lines) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(LINE);
        cell.setPaddingTop(8);
        cell.setPaddingBottom(12);
        cell.setPaddingRight(16);
        Paragraph head = new Paragraph(label, LABEL);
        head.setSpacingAfter(4);
        cell.addElement(head);
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;
            cell.addElement(new Paragraph(line, VALUE_SM));
        }
        return cell;
    }

    private PdfPTable buildItems(List<OrderItem> items) {
        PdfPTable table = new PdfPTable(new float[]{6, 1, 2, 2});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        addTh(table, "PRODUCTO", Element.ALIGN_LEFT);
        addTh(table, "CANT", Element.ALIGN_CENTER);
        addTh(table, "PRECIO", Element.ALIGN_RIGHT);
        addTh(table, "TOTAL", Element.ALIGN_RIGHT);

        boolean alt = false;
        for (OrderItem item : items) {
            Color bg = alt ? new Color(244, 240, 233) : PAPER;
            addTd(table, productLabel(item), Element.ALIGN_LEFT, bg);
            addTd(table, String.valueOf(item.getQuantity()), Element.ALIGN_CENTER, bg);
            addTd(table, money(item.getUnitPrice()), Element.ALIGN_RIGHT, bg);
            addTd(table, money(item.getTotalPrice()), Element.ALIGN_RIGHT, bg);
            alt = !alt;
        }
        return table;
    }

    private void addTh(PdfPTable table, String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, TH));
        cell.setBackgroundColor(INK);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        cell.setPadding(7);
        table.addCell(cell);
    }

    private void addTd(PdfPTable table, String text, int align, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, VALUE_SM));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(LINE);
        cell.setHorizontalAlignment(align);
        cell.setPadding(7);
        table.addCell(cell);
    }

    private PdfPTable buildTotals(Invoice invoice, Order order, String paymentMethod) {
        PdfPTable wrap = new PdfPTable(new float[]{3, 2});
        wrap.setWidthPercentage(100);
        wrap.setSpacingBefore(4);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setVerticalAlignment(Element.ALIGN_BOTTOM);
        Paragraph payLabel = new Paragraph("METODO DE PAGO", LABEL);
        payLabel.setSpacingAfter(4);
        left.addElement(payLabel);
        left.addElement(new Paragraph(paymentLabel(paymentMethod), VALUE_SM));
        wrap.addCell(left);

        PdfPTable totals = new PdfPTable(new float[]{3, 2});
        totals.setWidthPercentage(100);
        totalRow(totals, "Subtotal", money(invoice.getSubtotal()), false);
        totalRow(totals, "Envio", money(invoice.getShippingCost()), false);
        if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().signum() > 0) {
            totalRow(totals, discountLabel(order), "-" + money(invoice.getDiscountAmount()), false);
        }
        totalRow(totals, "TOTAL", money(invoice.getTotal()), true);

        PdfPCell right = new PdfPCell(totals);
        right.setBorder(Rectangle.NO_BORDER);
        wrap.addCell(right);

        return wrap;
    }

    private void totalRow(PdfPTable table, String label, String value, boolean grand) {
        PdfPCell l = new PdfPCell(new Phrase(label, grand ? GRAND_LABEL : TOTAL_LABEL));
        l.setHorizontalAlignment(Element.ALIGN_LEFT);
        l.setPaddingTop(grand ? 8 : 3);
        l.setPaddingBottom(3);
        l.setBorder(grand ? Rectangle.TOP : Rectangle.NO_BORDER);
        l.setBorderColor(LINE);

        PdfPCell v = new PdfPCell(new Phrase(value, grand ? GRAND_VALUE : TOTAL_VALUE));
        v.setHorizontalAlignment(Element.ALIGN_RIGHT);
        v.setPaddingTop(grand ? 8 : 3);
        v.setPaddingBottom(3);
        v.setBorder(grand ? Rectangle.TOP : Rectangle.NO_BORDER);
        v.setBorderColor(LINE);

        table.addCell(l);
        table.addCell(v);
    }

    private Paragraph buildFooter(Order order) {
        Paragraph foot = new Paragraph();
        foot.setSpacingBefore(36);
        foot.setAlignment(Element.ALIGN_CENTER);
        foot.add(new Phrase("Gracias por tu compra en K LAB. Este documento es una factura electronica valida.\n", FOOT));
        if (order.getNotes() != null && !order.getNotes().isBlank()) {
            foot.add(new Phrase("Nota: " + order.getNotes(), FOOT));
        }
        return foot;
    }

    private List<String> addressLines(Address address) {
        if (address == null) return List.of("-");
        String cityState = join(" ", address.getCity(), address.getState());
        return List.of(
                address.getStreet() != null ? address.getStreet() : "",
                cityState,
                join(" ", address.getCountry(), address.getPostalCode())
        );
    }

    private String join(String sep, String a, String b) {
        String x = a == null ? "" : a.trim();
        String y = b == null ? "" : b.trim();
        if (x.isEmpty()) return y;
        if (y.isEmpty()) return x;
        return x + sep + y;
    }

    private String discountLabel(Order order) {
        Coupon coupon = order.getCoupon();
        if (coupon != null && coupon.getCode() != null) {
            return "Cupon " + coupon.getCode();
        }
        return "Descuento";
    }

    private String paymentLabel(String method) {
        if (method == null) return "-";
        return switch (method) {
            case "CREDIT_CARD" -> "Tarjeta de credito";
            case "DEBIT_CARD" -> "Tarjeta de debito";
            case "BANK_TRANSFER" -> "Transferencia bancaria";
            case "CASH_ON_DELIVERY" -> "Pago contra entrega";
            default -> method;
        };
    }

    private String shortId(Order order) {
        return order.getId().toString().substring(0, 8).toUpperCase();
    }

    private String local(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(SV_ZONE).format(formatter);
    }

    private String money(BigDecimal value) {
        return "$" + (value != null ? value.toPlainString() : "0.00");
    }

    private String productLabel(OrderItem item) {
        String name = item.getProduct() != null ? item.getProduct().getName() : "Producto";
        if (item.getVariant() != null) {
            String size = item.getVariant().getSize();
            String color = item.getVariant().getColorName();
            if (size != null || color != null) {
                name = name + "  (" + (size != null ? size : "") + (color != null ? " " + color : "") + ")";
            }
        }
        return name;
    }

    private Paragraph spacer(float height) {
        Paragraph p = new Paragraph(" ");
        p.setLeading(height);
        return p;
    }

    private Image logo() {
        try {
            ClassPathResource resource = new ClassPathResource("invoice/logo.png");
            return Image.getInstance(resource.getInputStream().readAllBytes());
        } catch (Exception ex) {
            return null;
        }
    }

    private static class Watermark extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                ClassPathResource resource = new ClassPathResource("invoice/logo.png");
                Image mark = Image.getInstance(resource.getInputStream().readAllBytes());
                mark.scaleToFit(320, 320);
                float x = (PageSize.A4.getWidth() - mark.getScaledWidth()) / 2;
                float y = (PageSize.A4.getHeight() - mark.getScaledHeight()) / 2;
                mark.setAbsolutePosition(x, y);

                PdfContentByte canvas = writer.getDirectContentUnder();
                PdfGState state = new PdfGState();
                state.setFillOpacity(0.05f);
                canvas.saveState();
                canvas.setGState(state);
                canvas.addImage(mark);
                canvas.restoreState();
            } catch (Exception ignored) {
            }
        }
    }
}
