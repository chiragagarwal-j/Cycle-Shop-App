package com.cycles.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cycles.rest.dto.AddToCartRequest;
import com.cycles.rest.dto.CartUpdateRequest;
import com.cycles.rest.entity.Cart;
import com.cycles.rest.entity.Cycle;
import com.cycles.rest.entity.Order;
import com.cycles.rest.entity.User;
import com.cycles.rest.repository.CartRepository;
import com.cycles.rest.repository.CyclesRepository;
import com.cycles.rest.repository.OrderRepository;
import com.cycles.rest.repository.UserRepository;

@RestController
@RequestMapping("/api/cycles")
public class CyclesController {

    @Autowired
    private CyclesRepository cyclesRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    List<Cycle> getAllCycles() {
        return cyclesRepository.findAll();
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Cycle returnCycle(@PathVariable("id") Long id) {
        Optional<Cycle> cycle = cyclesRepository.findById(id);
        if (cycle.isPresent()) {
            Cycle c = cycle.get();
            if (c.getNumBorrowed() > 0) {
                c.setNumBorrowed(c.getNumBorrowed() - 1);
            }
            return cyclesRepository.save(c);
        } else {
            return null;
        }
    }

    @PostMapping("/{id}/restock")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @ResponseBody
    Cycle restockCycle(@PathVariable("id") Long id, @RequestParam("quantity") int quantity) {
        Optional<Cycle> cycle = cyclesRepository.findById(id);
        if (cycle.isPresent()) {
            Cycle c = cycle.get();
            c.setQuantity(c.getQuantity() + quantity);
            return cyclesRepository.save(c);
        } else {
            return null;
        }
    }

    @PostMapping("/addToCart")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        long id = request.getId();
        int quantity = request.getQuantity();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName()).orElse(null);

        if (user != null) {
            Optional<Cycle> cycle = cyclesRepository.findById(id);

            if (cycle.isPresent()) {
                Cycle c = cycle.get();

                Optional<Cart> existingCartItem = cartRepository.findByUserIDAndCycleIdAndOrdered(user.getId(), id,
                        false);

                if (existingCartItem.isPresent()) {
                    Cart cartItem = existingCartItem.get();
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cartRepository.save(cartItem);
                } else {
                    Cart cartItem = new Cart();
                    cartItem.setCycleId(id);
                    cartItem.setUserID(user.getId());
                    cartItem.setColor(c.getColor());
                    cartItem.setBrand(c.getBrand());
                    cartItem.setQuantity(quantity);
                    cartItem.setPrice(c.getPrice());

                    cartRepository.save(cartItem);
                }
                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cycle not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<List<Cart>> getCartItems() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByName(authentication.getName()).get();
        List<Cart> cartItems = cartRepository.findByUserID(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(cartItems);
    }

    @PostMapping("/confirmedOrder")
    public ResponseEntity<?> confirmedOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName()).orElse(null);

        if (user != null) {
            List<Cart> cartItems = cartRepository.findByUserID(user.getId());
            long totalPrice = 0;

            for (Cart cartItem : cartItems) {
                Cycle cycle = cyclesRepository.findById(cartItem.getCycleId()).orElse(null);

                if (cycle != null) {
                    int currentQuantity = cycle.getQuantity();
                    int orderedQuantity = cartItem.getQuantity();

                    if (currentQuantity >= orderedQuantity) {
                        cycle.setNumBorrowed(cycle.getNumBorrowed() + orderedQuantity);

                        cartItem.setOrdered(true);
                        cartRepository.save(cartItem);
                        cyclesRepository.save(cycle);

                        totalPrice += (orderedQuantity * cycle.getPrice());
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Insufficient quantity for cycle with ID: " + cycle.getId());
                    }
                }
            }

            Order order = new Order();
            order.setUser(user);
            order.setTotalPrice(totalPrice);
            orderRepository.save(order);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/updateCartItemQuantity")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> updateCartItemQuantity(@RequestBody CartUpdateRequest request) {
        long cycleId = request.getCycleId();
        int newQuantity = request.getNewQuantity();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName()).orElse(null);
        Optional<Cart> cartItemOptional = cartRepository.findByUserIDAndCycleIdAndOrdered(user.getId(), cycleId, false);

        if (cartItemOptional.isPresent()) {
            Cart cartItem = cartItemOptional.get();
            cartItem.setQuantity(newQuantity);
            cartRepository.save(cartItem);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
        }
    }

    @PostMapping("/removeFromCart")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@RequestBody Long cycleid) {
        long cycleId = cycleid;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName()).orElse(null);

        if (user != null) {
            Optional<Cart> cartItemOptional = cartRepository.findByUserIDAndCycleIdAndOrdered(user.getId(), cycleId,
                    false);

            if (cartItemOptional.isPresent()) {
                cartRepository.delete(cartItemOptional.get());
                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @GetMapping("/getBill")
    public ResponseEntity<List<Cart>> getBill() {
        List<Cart> orderedCartItems = cartRepository.findByOrdered(true);
        return ResponseEntity.status(HttpStatus.OK).body(orderedCartItems);
    }

}