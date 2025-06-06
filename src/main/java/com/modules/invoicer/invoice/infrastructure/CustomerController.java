package com.modules.invoicer.invoice.infrastructure;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.application.CustomerService;
import com.modules.invoicer.user.application.UserService;
import com.modules.invoicer.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @Autowired
    public CustomerController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
    }

    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customer-list"; // Assuming you have a Thymeleaf template at src/main/resources/templates/customers/list.html
    }

    @GetMapping("/new")
    public String showNewCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {
        User userNewCustomer = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userNewCustomer = userService.findByUsername(authentication.getName()).get();

        }

        customer.setUser(userNewCustomer);
        customerService.save(customer);
        return "redirect:/customers";
    }
}
