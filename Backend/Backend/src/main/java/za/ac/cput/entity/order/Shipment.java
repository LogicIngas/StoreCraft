package za.ac.cput.entity.order;


import jakarta.persistence.*;
import za.ac.cput.entity.order.Order;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String carrier;
    private String trackingNumber;
    private String status;
    private LocalDateTime shippedDate;
    private LocalDateTime estimatedDeliveryDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public Shipment() {
    }

    protected Shipment(Builder builder) {
        this.id = builder.id;
        this.carrier = builder.carrier;
        this.trackingNumber = builder.trackingNumber;
        this.status = builder.status;
        this.shippedDate = builder.shippedDate;
        this.estimatedDeliveryDate = builder.estimatedDeliveryDate;
        this.order = builder.order;
    }

    public String getId() {
        return id;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(id, shipment.id) && Objects.equals(carrier, shipment.carrier) && Objects.equals(trackingNumber, shipment.trackingNumber) && Objects.equals(status, shipment.status) && Objects.equals(shippedDate, shipment.shippedDate) && Objects.equals(estimatedDeliveryDate, shipment.estimatedDeliveryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carrier, trackingNumber, status, shippedDate, estimatedDeliveryDate);
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "id='" + id + '\'' +
                ", carrier='" + carrier + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", status='" + status + '\'' +
                ", shippedDate=" + shippedDate +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate +
                '}';
    }

    public static class Builder {
        private String id;
        private String carrier;
        private String trackingNumber;
        private String status;
        private LocalDateTime shippedDate;
        private LocalDateTime estimatedDeliveryDate;
        private Order order;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setCarrier(String carrier) {
            this.carrier = carrier;
            return this;
        }

        public Builder setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setShippedDate(LocalDateTime shippedDate) {
            this.shippedDate = shippedDate;
            return this;
        }

        public Builder setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }

        public Builder setOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder copy(Shipment shipment) {
            this.id = shipment.id;
            this.carrier = shipment.carrier;
            this.trackingNumber = shipment.trackingNumber;
            this.status = shipment.status;
            this.shippedDate = shipment.shippedDate;
            this.estimatedDeliveryDate = shipment.estimatedDeliveryDate;
            this.order = shipment.order;
            return this;
        }

        public Shipment build() {
            return new Shipment(this);
        }
    }
}