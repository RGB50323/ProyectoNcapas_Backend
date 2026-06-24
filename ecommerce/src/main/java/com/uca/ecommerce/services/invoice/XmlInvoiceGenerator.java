package com.uca.ecommerce.services.invoice;

import com.uca.ecommerce.domain.entities.Address;
import com.uca.ecommerce.domain.entities.Coupon;
import com.uca.ecommerce.domain.entities.Invoice;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class XmlInvoiceGenerator {

    private static final ZoneId SV_ZONE = ZoneId.of("America/El_Salvador");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public byte[] generate(Invoice invoice, Order order, List<OrderItem> items, String paymentMethod) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<FacturaElectronica>\n");
        xml.append("  <Emisor>K LAB by Mister K</Emisor>\n");
        xml.append("  <NumeroControl>").append(esc(invoice.getControlNumber())).append("</NumeroControl>\n");
        xml.append("  <FechaEmision>").append(local(invoice.getIssuedAt())).append("</FechaEmision>\n");
        xml.append("  <Pedido>\n");
        xml.append("    <Id>").append(order.getId()).append("</Id>\n");
        if (order.getCreatedAt() != null) {
            xml.append("    <Fecha>").append(local(order.getCreatedAt())).append("</Fecha>\n");
        }
        xml.append("    <MetodoPago>").append(esc(paymentMethod)).append("</MetodoPago>\n");
        if (order.getShippingMethod() != null) {
            xml.append("    <MetodoEnvio>").append(esc(order.getShippingMethod().getName())).append("</MetodoEnvio>\n");
            xml.append("    <EntregaEstimada>").append(esc(order.getShippingMethod().getEta())).append("</EntregaEstimada>\n");
        }
        xml.append("  </Pedido>\n");
        xml.append("  <Cliente>\n");
        xml.append("    <Nombre>").append(esc(invoice.getCustomerName())).append("</Nombre>\n");
        xml.append("    <Correo>").append(esc(invoice.getCustomerEmail())).append("</Correo>\n");
        appendAddress(xml, order.getShippingAddress());
        xml.append("  </Cliente>\n");
        xml.append("  <Detalle>\n");
        for (OrderItem item : items) {
            xml.append("    <Linea>\n");
            xml.append("      <Producto>").append(esc(productLabel(item))).append("</Producto>\n");
            xml.append("      <Cantidad>").append(item.getQuantity()).append("</Cantidad>\n");
            xml.append("      <PrecioUnitario>").append(item.getUnitPrice()).append("</PrecioUnitario>\n");
            xml.append("      <Total>").append(item.getTotalPrice()).append("</Total>\n");
            xml.append("    </Linea>\n");
        }
        xml.append("  </Detalle>\n");
        xml.append("  <Resumen>\n");
        xml.append("    <Subtotal>").append(invoice.getSubtotal()).append("</Subtotal>\n");
        xml.append("    <Envio>").append(invoice.getShippingCost()).append("</Envio>\n");
        Coupon coupon = order.getCoupon();
        if (coupon != null && coupon.getCode() != null) {
            xml.append("    <Cupon>").append(esc(coupon.getCode())).append("</Cupon>\n");
        }
        xml.append("    <Descuento>").append(invoice.getDiscountAmount()).append("</Descuento>\n");
        xml.append("    <Total>").append(invoice.getTotal()).append("</Total>\n");
        xml.append("  </Resumen>\n");
        xml.append("</FacturaElectronica>\n");
        return xml.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendAddress(StringBuilder xml, Address address) {
        if (address == null) return;
        xml.append("    <Direccion>\n");
        xml.append("      <Calle>").append(esc(address.getStreet())).append("</Calle>\n");
        xml.append("      <Ciudad>").append(esc(address.getCity())).append("</Ciudad>\n");
        xml.append("      <Departamento>").append(esc(address.getState())).append("</Departamento>\n");
        xml.append("      <Pais>").append(esc(address.getCountry())).append("</Pais>\n");
        xml.append("    </Direccion>\n");
    }

    private String productLabel(OrderItem item) {
        String name = item.getProduct() != null ? item.getProduct().getName() : "Producto";
        if (item.getVariant() != null) {
            String size = item.getVariant().getSize();
            String color = item.getVariant().getColorName();
            if (size != null || color != null) {
                name = name + " (" + (size != null ? size : "") + (color != null ? " " + color : "") + ")";
            }
        }
        return name;
    }

    private String local(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(SV_ZONE).format(DATE);
    }

    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
