package com.modules.invoicer.invoice.infrastructure;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.application.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customer-list"; // Assuming you have a Thymeleaf template at src/main/resources/templates/customers/list.html
    }

    // You can add methods for adding, editing, deleting customers here
}
