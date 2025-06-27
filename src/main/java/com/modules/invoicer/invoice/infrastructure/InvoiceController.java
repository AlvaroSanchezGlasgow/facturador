package com.modules.invoicer.invoice.infrastructure;

import com.modules.invoicer.invoice.application.InvoiceService;
import com.modules.invoicer.invoice.application.InvoiceNoteService;
import com.modules.invoicer.invoice.application.InvoicePdfService;
import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import com.modules.invoicer.invoice.domain.InvoiceNote;
import com.modules.invoicer.invoice.domain.InvoiceStatus;
import com.modules.invoicer.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final InvoiceNoteService invoiceNoteService;
    private final InvoicePdfService pdfService;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    public InvoiceController(InvoiceService invoiceService, InvoiceNoteService invoiceNoteService, InvoicePdfService pdfService) {
        this.invoiceService = invoiceService;
        this.invoiceNoteService = invoiceNoteService;
        this.pdfService = pdfService;

    }

    @GetMapping
    public String listInvoices(Model model, @AuthenticationPrincipal User currentUser) {
        logger.info("Listing invoices for user {}", currentUser.getUsername());
        List<Invoice> invoices = invoiceService.findInvoicesByUserAsync(currentUser).join();
        model.addAttribute("invoices", invoices);
        return "invoice-list";
    }

    @GetMapping("/new")
    public String showCreateInvoiceForm(Model model, @AuthenticationPrincipal User currentUser) {
        logger.info("Showing create invoice form for user {}", currentUser.getUsername());
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

        logger.info("Creating invoice for user {}", currentUser.getUsername());
        invoiceService.createInvoiceAsync(invoice, currentUser).join();
        redirectAttributes.addFlashAttribute("successMessage", "Factura creada exitosamente!");
        return "redirect:/invoices";

    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        logger.info("Viewing invoice {}", id);
        Optional<Invoice> invoiceOptional = invoiceService.findInvoiceByIdAndUserAsync(id, currentUser).join();
        if (invoiceOptional.isEmpty()) {
            return "redirect:/invoices";
        }
        Invoice invoice = invoiceOptional.get();
        model.addAttribute("invoice", invoice);
        model.addAttribute("notes", invoiceNoteService.findNotesByInvoice(invoice));
        model.addAttribute("newNote", InvoiceNote.builder().build());
        return "invoice-view";
    }

    @PostMapping("/{id}/notes")
    public String addNote(@PathVariable Long id,
                          @Valid @ModelAttribute("newNote") InvoiceNote newNote,
                          BindingResult result,
                          @AuthenticationPrincipal User currentUser,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Contenido de nota inválido");
            return "redirect:/invoices/" + id;
        }
        invoiceNoteService.addNote(id, newNote.getContent(), currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Nota añadida correctamente");
        return "redirect:/invoices/" + id;
    }

    @GetMapping("/{id}/edit")
    public String showEditInvoiceForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        logger.info("Showing edit form for invoice {}", id);
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
            logger.info("Updating invoice {}", id);
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
            logger.info("Deleting invoice {}", id);
            invoiceService.deleteInvoiceAsync(id, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Factura eliminada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la factura: " + e.getMessage());
        }
        return "redirect:/invoices";
    }

    @PostMapping("/{id}/send-verifactu")
    public String sendInvoiceToVerifactu(@PathVariable Long id,
                                         @AuthenticationPrincipal User currentUser,
                                         RedirectAttributes redirectAttributes) {
        try {
            logger.info("Sending invoice {} to Verifactu", id);
            invoiceService.sendInvoiceToVerifactu(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Factura enviada a Verifactu");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar la factura: " + e.getMessage());
        }
        return "redirect:/invoices";
    }

    @PostMapping("/{id}/status")
    public String updateInvoiceStatus(@PathVariable Long id,
                                      @RequestParam("status") InvoiceStatus status,
                                      @AuthenticationPrincipal User currentUser,
                                      RedirectAttributes redirectAttributes) {
        try {
            logger.info("Updating status of invoice {} to {}", id, status);
            invoiceService.updateInvoiceStatus(id, status, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Estado actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el estado: " + e.getMessage());
        }
        return "redirect:/invoices/" + id;
    }

    @PostMapping("/addItem")
    public String addItemToInvoice(@ModelAttribute Invoice invoice) {
        logger.info("Adding item to invoice");
        invoice.addItem(new InvoiceItem());
        return "invoice-form :: #invoiceItems"; // Fragmento para Thymeleaf
    }

    @PostMapping("/removeItem/{index}")
    public String removeItemFromInvoice(@ModelAttribute Invoice invoice, @PathVariable int index) {
        logger.info("Removing item {} from invoice", index);
        if (index >= 0 && index < invoice.getItems().size()) {
            invoice.getItems().remove(index);
        }
        return "invoice-form :: #invoiceItems"; // Fragmento para Thymeleaf
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id,
                                                     @AuthenticationPrincipal User currentUser) {
        logger.info("Downloading PDF for invoice {}", id);
        Optional<Invoice> invoiceOptional = invoiceService.findInvoiceByIdAndUserAsync(id, currentUser).join();
        if (invoiceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        byte[] pdf = pdfService.generateInvoicePdf(invoiceOptional.get());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "factura-" + invoiceOptional.get().getInvoiceNumber() + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    // Customer management (simplified for now, could be its own controller)
    @GetMapping("/customers")
    public String listCustomers(Model model, @AuthenticationPrincipal User currentUser) {
        logger.info("Listing customers for invoice module");
        model.addAttribute("customers", invoiceService.findCustomersByUserAsync(currentUser).join());
        return "customers/list";
    }

    @GetMapping("/customers/new")
    public String showCreateCustomerForm(Model model) {
        logger.info("Showing create customer form from invoice module");
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
            logger.info("Creating customer {} from invoice module", customer.getNif());
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
        logger.info("Showing edit customer form for id {}", id);
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
            logger.info("Updating customer {}", id);
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
            logger.info("Deleting customer {}", id);
            invoiceService.deleteCustomerAsync(id, currentUser).join();
            redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el cliente: " + e.getMessage());
        }
        return "redirect:/invoices/customers";
    }
}
