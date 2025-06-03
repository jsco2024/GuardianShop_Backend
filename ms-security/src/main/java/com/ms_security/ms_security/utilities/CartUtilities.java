//package com.ms_security.ms_security.utilities;
//
//import com.ms_security.ms_security.persistence.entity.CartEntity;
//import com.ms_security.ms_security.persistence.entity.InventoryEntity;
//import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
//import com.ms_security.ms_security.service.impl.consultations.CartConsultations;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class CartUtilities {
//    private final CartConsultations _cartConsultations;  // Inyección de dependencias
//
//    public void addItemToCart(CartEntity cartEntity, InventoryEntity inventory, Long quantity) {
//        // Verificar si los items del carrito son nulos e inicializar la lista si es necesario
//        if (cartEntity.getItems() == null) {
//            cartEntity.setItems(new ArrayList<>());
//        }
//
//        boolean itemExists = false;
//        List<OrderItemEntity> itemsToRemove = new ArrayList<>();
//
//        // Iterar sobre los items del carrito
//        for (OrderItemEntity item : cartEntity.getItems()) {
//            if (item.getProduct().getId().equals(inventory.getId())) {
//                // Si el item ya existe, actualizar su cantidad
//                item.setQuantity(item.getQuantity() + quantity);
//                // Si la cantidad es menor o igual a 0, marcar el item para eliminarlo
//                if (item.getQuantity() <= 0) {
//                    itemsToRemove.add(item);
//                }
//                itemExists = true;
//                break;
//            }
//        }
//
//        // Eliminar los items con cantidad <= 0
//        cartEntity.getItems().removeAll(itemsToRemove);
//
//        // Si el item no existía en el carrito, agregarlo como nuevo
//        if (!itemExists) {
//            OrderItemEntity newItem = new OrderItemEntity();
//            newItem.setProduct(inventory);
//            newItem.setQuantity(quantity);
//
//            cartEntity.getItems().add(newItem);
//        }
//    }
//
//    public boolean removeItemFromCart(CartEntity cartEntity, Long inventoryId) {
//        // Verificar si el carrito y su lista de items no son nulos
//        if (cartEntity != null && cartEntity.getItems() != null) {
//            // Remover el ítem correspondiente del carrito
//            boolean removed = cartEntity.getItems().removeIf(item -> item.getProduct().getId().equals(inventoryId));
//
//            // Si se eliminó un ítem, es necesario actualizar el carrito en la base de datos
//            if (removed) {
//                // Actualizar el carrito en la base de datos
//                _cartConsultations.updateData(cartEntity);
//            }
//
//            return removed;
//        }
//        return false;
//    }
//
//    public Optional<CartEntity> getCart(Long cartId) {
//        return _cartConsultations.findById(cartId);
//    }
//}
