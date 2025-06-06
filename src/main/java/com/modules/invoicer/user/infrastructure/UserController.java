package com.modules.invoicer.user.infrastructure;

import com.modules.invoicer.invoice.application.InvoiceService;
import com.modules.invoicer.user.application.UserService;
import com.modules.invoicer.user.domain.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class UserController {

    private final UserService userService;
    private final InvoiceService invoiceService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, InvoiceService invoiceService) {
        this.userService = userService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/")
    public String showRedirection() {
        logger.info("Redirecting to dashboard");
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Showing registration form");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            logger.info("Registering user {}", user.getUsername());
            userService.registerNewUserAsync(user).join();
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Showing login form");
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        logger.info("Showing dashboard");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsernameAsync(username)
                .join()
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("pendingInvoicesCount", invoiceService.countPendingInvoicesAsync(currentUser).join());
        model.addAttribute("paidInvoicesCount", invoiceService.countPaidInvoicesAsync(currentUser).join());
        model.addAttribute("totalCustomersCount", invoiceService.countCustomersAsync(currentUser).join());
        model.addAttribute("invoices", invoiceService.findLatestInvoicesAsync(currentUser).join());

        return "dashboard";
    }
}