package com.modules.invoicer.invoice.infrastructure;

import com.modules.invoicer.invoice.application.InvoiceService;
import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import com.modules.invoicer.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public String listInvoices(Model model, @AuthenticationPrincipal User currentUser) {
        List<Invoice> invoices = invoiceService.findInvoicesByUserAsync(currentUser).join();
        model.addAttribute("invoices", invoices);
        return "invoice-list";
    }

    @GetMapping("/new")
    public String showCreateInvoiceForm(Model model, @AuthenticationPrincipal User currentUser) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDate.now());
        invoice.addItem(new InvoiceItem()); // Añadir un item por defecto
        model.addAttribute("invoice", invoice);
        model.addAttribute("customers", invoiceService.findCustomersByUserAsync(currentUser).join());
        return "invoice-form";
    }

    @PostMapping("/new")
    public String createInvoice(@Valid @ModelAttribute("invoice") Invoice invoice, BindingResult result,
                                @AuthenticationPrincipal User currentUser, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customers", invoiceService.findCustomersByUser(currentUser));
            return "invoice-form";
        }

            invoiceService.createInvoiceAsync(invoice, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Factura creada exitosamente!");
            return "redirect:/invoices";

    }

    @GetMapping("/{id}/edit")
    public String showEditInvoiceForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Optional<Invoice> invoiceOptional = invoiceService.findInvoiceByIdAndUserAsync(id, currentUser).join();
        if (invoiceOptional.isEmpty()) {
            return "redirect:/invoices";
        }
        model.addAttribute("invoice", invoiceOptional.get());
        model.addAttribute("customers", invoiceService.findCustomersByUserAsync(currentUser).join());
        return "invoice-form";
    }

    @PostMapping("/{id}/edit")
    public String updateInvoice(@PathVariable Long id, @Valid @ModelAttribute("invoice") Invoice invoice, BindingResult result,
                                @AuthenticationPrincipal User currentUser, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customers", invoiceService.findCustomersByUser(currentUser));
            return "invoice-form";
        }
        try {
            invoice.setId(id); // Asegurar que el ID se mantenga
            invoiceService.updateInvoiceAsync(invoice, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Factura actualizada exitosamente!");
            return "redirect:/invoices";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al actualizar la factura: " + e.getMessage());
            model.addAttribute("customers", invoiceService.findCustomersByUser(currentUser));
            return "invoice-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteInvoice(@PathVariable Long id, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            invoiceService.deleteInvoiceAsync(id, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Factura eliminada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la factura: " + e.getMessage());
        }
        return "redirect:/invoices";
    }

    @PostMapping("/addItem")
    public String addItemToInvoice(@ModelAttribute Invoice invoice) {
        invoice.addItem(new InvoiceItem());
        return "invoice-form :: #invoiceItems"; // Fragmento para Thymeleaf
    }

    @PostMapping("/removeItem/{index}")
    public String removeItemFromInvoice(@ModelAttribute Invoice invoice, @PathVariable int index) {
        if (index >= 0 && index < invoice.getItems().size()) {
            invoice.getItems().remove(index);
        }
        return "invoice-form :: #invoiceItems"; // Fragmento para Thymeleaf
    }

    // Customer management (simplified for now, could be its own controller)
    @GetMapping("/customers")
    public String listCustomers(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("customers", invoiceService.findCustomersByUserAsync(currentUser).join());
        return "customers/list";
    }

    @GetMapping("/customers/new")
    public String showCreateCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @PostMapping("/customers/new")
    public String createCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult result,
                                 @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        try {
            invoiceService.createOrUpdateCustomerAsync(customer, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Cliente creado exitosamente!");
            return "redirect:/invoices/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el cliente: " + e.getMessage());
            return "customers/form";
        }
    }

    @GetMapping("/customers/{id}/edit")
    public String showEditCustomerForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Optional<Customer> customerOptional = invoiceService.findCustomerByIdAndUserAsync(id, currentUser).join();
        if (customerOptional.isEmpty()) {
            return "redirect:/invoices/customers";
        }
        model.addAttribute("customer", customerOptional.get());
        return "customers/form";
    }

    @PostMapping("/customers/{id}/edit")
    public String updateCustomer(@PathVariable Long id, @Valid @ModelAttribute("customer") Customer customer, BindingResult result,
                                 @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        try {
            customer.setId(id);
            invoiceService.createOrUpdateCustomerAsync(customer, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado exitosamente!");
            return "redirect:/invoices/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el cliente: " + e.getMessage());
            return "customers/form";
        }
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            invoiceService.deleteCustomerAsync(id, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el cliente: " + e.getMessage());
        }
        return "redirect:/invoices/customers";
    }
}
