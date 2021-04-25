/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 30 - 12 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.*;
import com.eyob.IFRSSystem.repository.*;
import com.eyob.IFRSSystem.web.dto.BillRegistrationDto;
import com.eyob.IFRSSystem.web.dto.InvoiceRegistrationDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value = {"", "/entity", "/pages", "/userPages", "/pages/userPages"})
public class BillandInvoiceController {


    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AccountRepository accountRepository;

    private ModelMapper modelMapper;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    UserRepository userRepository;
    boolean issmw = false;

    public void logeduser(String entity_id, Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else if (role.contains("ROLE_USER"))
            model.addAttribute("role", "Accountant");
        else
            model.addAttribute("role", "Reviewer");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        model.addAttribute("entity_id", entity_id);
        Long entity_admin = Long.valueOf(1);
        for (entity entity : entityRepository.findAll()) {
            if (entity.getEntity_id().equals(entity_id))
                entity_admin = entity.getAdmin_id();
        }

        if (!userRepository.findByEmail(principal.getName()).getId().equals(entity_admin) || !user.getEnabled()
        || !user.getApproved())
            issmw = true;

        model.addAttribute("user",user);

    }

    @GetMapping("{entity_id}/bill")
    private String getAll(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged in user
        ///******************
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ArrayList<bill> bills = new ArrayList<>();
        for (bill bill : billRepository.findAll()) {
            if (bill.getBill_id().contains(entity_id)){
                for(vendor vendor: vendorRepository.findAll()){
                    if (vendor.getVendor_id().equals(bill.getVendor_id()))
                        bill.setVendor_id(vendor.getVendor_name());
                }
                bills.add(bill);
            }
        }

        model.addAttribute("bills", bills);
        model.addAttribute("billSize", bills.size());


        return "pages/userPages/bill";

    }

    @ModelAttribute("newBill")
    public BillRegistrationDto newBill() {
        return new BillRegistrationDto();
    }

    @GetMapping("{entity_id}/addBill")
    private String add_bill(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        List<vendor> allvendors = (List) vendorRepository.findAll();

        ArrayList<String> vendors = new ArrayList<>();

        for (vendor v : allvendors) {
            if (v.getVendor_id().contains(entity_id))
                vendors.add(v.getVendor_name());
        }

        List<account> allAccounts = (List) accountRepository.findAll();

        List<String> cashAccounts = new ArrayList<>();
        List<String> receivableAccounts = new ArrayList<>();
        ArrayList<String> payableAccounts = new ArrayList<>();
        ArrayList<String> earningAccounts = new ArrayList<>();
        List<String> terms = Arrays.asList(new String[]{"Due on Receipt", "Net 30 days", "Net 60 Days", "Net 90 Days"});
        for (account a : allAccounts) {
            if (a.getAccount_id().contains(entity_id)) {
                String name = a.getName();

                if (name.contains("Intangible") || a.getRole().contains("cash") ||
                        (a.getRole().contains("ppe") && a.getBalance_type().equals("debit"))
                        || name.contains("Investment") || (a.getRole().contains("_loan")))
                    cashAccounts.add(name);
                if (name.contains("receivable") || name.contains("Receivable"))
                    receivableAccounts.add(name);
                if ((name.contains("payable") || name.contains("Payable")) && Double.parseDouble(a.getCode()) < 3000.0)
                    payableAccounts.add(name);
                if ((!a.getRole().startsWith("in") &&
                        (!(name.contains("revenue") || name.contains("Revenue")) && Double.parseDouble(a.getCode()) > 3000.0))
                        || a.getRole().contains("_ppe") || a.getRole().contains("cash"))
                    earningAccounts.add(name);
            }
        }

        model.addAttribute("entity_id", entity_id);
        model.addAttribute("vendors", vendors);
        model.addAttribute("terms", terms);
        model.addAttribute("cash_accounts", cashAccounts);
        model.addAttribute("receivable_accounts", receivableAccounts);
        model.addAttribute("payable_accounts", payableAccounts);
        model.addAttribute("earning_accounts", earningAccounts);

        return "pages/userPages/newBill";
    }

    @PostMapping("{entity_id}/addBill")
    private String add(@ModelAttribute("newBill") @Valid BillRegistrationDto newBill, @PathVariable("entity_id") String entity_id,
                       Model model, BindingResult result) {
        bill newbill = new bill();
        newbill.setBill_number(newBill.getXrn());
        String vendorName = newBill.getVendor();
        List<vendor> vendors = (List) vendorRepository.findAll();
        for (vendor v : vendors) {
            if (v.getVendor_name().equals(vendorName)) {
                newbill.setVendor_id(v.getVendor_id());
            }
        }
        newbill.setAmount_due(newBill.getAmount_due());
        newbill.setDue_date((newBill.getBill_date()));
        newbill.setTerms(newBill.getTerm());
        Date date = new Date(System.currentTimeMillis());
        newbill.setCreated(date);
        newbill.setDate(date);
        newbill.setCash_account_id(accountRepository.findByName(newBill.getCash_account()).getAccount_id());
        newbill.setReceivable_account_id(accountRepository.findByName(newBill.getReceivable_account()).getAccount_id());
        newbill.setPayable_account_id(accountRepository.findByName(newBill.getPayable_account()).getAccount_id());
        newbill.setEarnings_account_id(accountRepository.findByName(newBill.getEarning_account()).getAccount_id());
        String id = entity_id + "_bill_" + getAlphaNumericString(5);
        newbill.setBill_id(id);
        newbill.setBill_number("Bill_" + getAlphaNumericString(5));

        ledger ledger = new ledger();
        String led_id = entity_id + getAlphaNumericString(5);
        ledger.setLedger_id(led_id);
        ledger.setCreated(date);
        ledger.setName("Bill " + id);
        ledger.setEntity_id(entity_id);
        ledgerRepository.save(ledger);

        newbill.setLedger_id(led_id);


        if (newBill.getTerm().contains("Due")) {
            journalEntry journalEntry = new journalEntry();
            String jou_id = entity_id + "_" + getAlphaNumericString(5);
            journalEntry.setJe_id(jou_id);
            journalEntry.setDate(date);
            journalEntry.setCreated(date);
            journalEntry.setActivity("operating"); // needs editing
            journalEntry.setLedger_id(led_id);
            journalentryRepository.save(journalEntry);

            transaction transactionDebit = new transaction();
            transactionDebit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
            transactionDebit.setTx_type("debit");
            transactionDebit.setAccount_id(newbill.getCash_account_id());
            transactionDebit.setAmount(newbill.getAmount_due());
            transactionDebit.setJournal_entry_id(jou_id);
            transactionDebit.setCreated(date);

            transaction transactionCredit = new transaction();
            transactionCredit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
            transactionCredit.setTx_type("credit");

            /* check here
            if (newBill.getEarning_account().contains("cash")) {
                transactionCredit.setAmount(newbill.getAmount_due() * -1);
            } else
                */
            transactionCredit.setAmount(newbill.getAmount_due());
            transactionCredit.setAccount_id(newbill.getEarnings_account_id());
            transactionCredit.setJournal_entry_id(jou_id);
            transactionCredit.setCreated(date);

            transactionRepository.save(transactionCredit);
            transactionRepository.save(transactionDebit);

            newbill.setAmount_paid(newbill.getAmount_due());
            newbill.setPaid(true);
        }

        billRepository.save(newbill);

        model.addAttribute("entity_id", entity_id);
        return "redirect:/{entity_id}/bill";
    }

    public static String getAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    @GetMapping("{entity_id}/detailBill/{bill_id}")
    private String detail_bill(@PathVariable("entity_id") String entity_id, @PathVariable("bill_id") String bill_id,
                               Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        bill bill = billRepository.findById(bill_id).get();
        vendor vendor = vendorRepository.findById(bill.getVendor_id()).get();

        if (bill.getAmount_paid() == null) {
            bill.setAmount_paid(0.0);
            bill.setAmount_unearned(bill.getAmount_due());
        } else
            bill.setAmount_unearned(bill.getAmount_due() - bill.getAmount_paid());
        bill.setProgress(round((bill.getAmount_paid() * 100) / bill.getAmount_due(), 2));

        Double credit = 0.0, debit = 0.0;

        ArrayList<transaction> transactions = new ArrayList<>();
        for (journalEntry journalEntry : journalentryRepository.findAll()) {
            if (journalEntry.getLedger_id().equals(bill.getLedger_id())) {
                for (transaction transaction : transactionRepository.findAll()) {
                    if (transaction.getJournal_entry_id().equals(journalEntry.getJe_id())) {
                        account account = accountRepository.findById(transaction.getAccount_id()).get();
                        transaction.setAccount_id(account.getName());
                        if (transaction.getTx_type().equals("credit"))
                            credit = credit + transaction.getAmount();
                        else
                            debit = debit + transaction.getAmount();
                        transactions.add(transaction);
                    }
                }
            }
        }

        model.addAttribute("credit", credit);
        model.addAttribute("debit", debit);
        model.addAttribute("transactions", transactions);
        model.addAttribute("vendor", vendor);
        model.addAttribute("bill", bill);
        model.addAttribute("entity_id", entity_id);

        return "pages/userPages/billDetail";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @GetMapping("{entity_id}/updateBill/{bill_id}")
    private String update_bill(@PathVariable("entity_id") String entity_id, @PathVariable("bill_id") String bill_id,
                               Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        bill bill = billRepository.findById(bill_id).get();
        vendor vendor = vendorRepository.findById(bill.getVendor_id()).get();


        model.addAttribute("vendor", vendor);
        model.addAttribute("bill", bill);
        model.addAttribute("terms", bill.getTerms());
        model.addAttribute("account_cash", accountRepository.findById(bill.getCash_account_id()).get().getName());
        model.addAttribute("amount_cash", bill.getAmount_paid());
        model.addAttribute("account_receivable", accountRepository.findById(bill.getReceivable_account_id()).get().getName());
        if (bill.getAmount_paid() == null) {
            bill.setAmount_paid(0.00);

        }
        model.addAttribute("amount_payable", bill.getAmount_due() - bill.getAmount_paid());
        model.addAttribute("account_payable", accountRepository.findById(bill.getPayable_account_id()).get().getName());
        model.addAttribute("amount_receivable", 0);
        model.addAttribute("account_earning", accountRepository.findById(bill.getEarnings_account_id()).get().getName());
        model.addAttribute("amount_earning", bill.getAmount_paid());
        model.addAttribute("entity_id", entity_id);
        return "pages/userPages/billUpdate";
    }


    @PostMapping("{entity_id}/updateBill/{bill_id}")
    private String update_bills(@ModelAttribute("bill") bill newBill, @PathVariable("entity_id") String entity_id, @PathVariable("bill_id") String bill_id,
                                Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        Date date = new Date(System.currentTimeMillis());
        bill updateBill = billRepository.findById(bill_id).get();
        Double amountPaid = updateBill.getAmount_paid();


        if (!transactionRepository.findById(entity_id + "_" + "AP").isPresent()) {
            transaction transaction = new transaction();
            transaction.setTransaction_id(entity_id + "_AP");
            transaction.setAmount(updateBill.getAmount_due());
            transaction.setDescription(entity_id + " Accounts Payables");
            transaction.setAccount_id(updateBill.getPayable_account_id());
            transaction.setCreated(date);
            transaction.setTx_type("notDefined");
            transaction.setJournal_entry_id("notDefined");
            transactionRepository.save(transaction);
        } else if (updateBill.getAmount_paid() == null || updateBill.getAmount_paid() == 0.0) {
            transaction transaction = transactionRepository.findById(entity_id + "_AP").get();
            transaction.setAmount(transaction.getAmount() + updateBill.getAmount_due());
            transactionRepository.save(transaction);
        }

        updateBill.setAmount_paid(newBill.getAmount_paid());
        updateBill.setUpdated(newBill.getDate());


        journalEntry journalEntry = new journalEntry();
        String jou_id = entity_id + "_" + getAlphaNumericString(5);
        journalEntry.setJe_id(jou_id);
        journalEntry.setDate(newBill.getDate());
        journalEntry.setCreated(date);
        journalEntry.setActivity("operating"); // needs editing
        journalEntry.setLedger_id(updateBill.getLedger_id());
        journalentryRepository.save(journalEntry);

        transaction transactionCredit = new transaction();
        transactionCredit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
        transactionCredit.setTx_type("credit");
        transactionCredit.setAccount_id(updateBill.getEarnings_account_id());
        if (amountPaid == null)
            amountPaid = 0.0;
        transactionCredit.setAmount(updateBill.getAmount_paid() - amountPaid);
        transactionCredit.setJournal_entry_id(jou_id);
        transactionCredit.setCreated(date);

        transaction transactionDebit = new transaction();
        transactionDebit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
        transactionDebit.setTx_type("debit");
        transactionDebit.setAccount_id(updateBill.getCash_account_id());
        transactionDebit.setAmount(updateBill.getAmount_paid() - amountPaid);
        transactionDebit.setJournal_entry_id(jou_id);
        transactionDebit.setCreated(date);

        transaction payable = transactionRepository.findById(entity_id + "_AP").get();
        payable.setAmount(payable.getAmount() - (updateBill.getAmount_paid() - amountPaid));
        transactionRepository.save(payable);

        transactionRepository.save(transactionCredit);
        transactionRepository.save(transactionDebit);
        if (updateBill.getAmount_due() - updateBill.getAmount_paid() == 0.0)
            updateBill.setPaid(true);
        billRepository.save(updateBill);

        model.addAttribute("entity_id", entity_id);
        return "redirect:/{entity_id}/bill";
    }

    @GetMapping("{entity_id}/deleteBill/{bill_id}")
    private String delete_bill( @PathVariable("entity_id") String entity_id, @PathVariable("bill_id") String bill_id,
                                   Model model, Principal principal) {

        ///****** this part is always logged user
        String firstName = userRepository.findByEmail(principal.getName()).getFirstName();
        String lastName = userRepository.findByEmail(principal.getName()).getLastName();
        String role = userRepository.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        ///******************

        bill bill = billRepository.findById(bill_id).get();
        Double paid = 0.0, unpaid = 0.0, due = 0.0;
        paid = bill.getAmount_paid(); due = bill.getAmount_due(); unpaid = due - paid;
        String ledger_id = bill.getLedger_id();
        ledger ledger = ledgerRepository.findById(bill.getLedger_id()).get();


        for (journalEntry journalEntry: journalentryRepository.findAll()){
            if( journalEntry.getLedger_id().equals(ledger_id)) {
                for (transaction transaction: transactionRepository.findAll()){
                    if(transaction.getJournal_entry_id().equals(journalEntry.getJe_id()))
                        transactionRepository.delete(transaction);
                }
                journalentryRepository.delete(journalEntry);
            }
        }

        if(unpaid != 0){
            transaction transaction = transactionRepository.findById(entity_id+"_AP").get();
            transaction.setAmount(transaction.getAmount() - unpaid);
            transactionRepository.save(transaction);
        }



        billRepository.delete(bill);
        ledgerRepository.delete(ledger);

        model.addAttribute("entity_id",entity_id);

        return "redirect:/{entity_id}/bill";
    }

    /*******************************************************************************************

     Invoice


     *////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("{entity_id}/invoice")
    private String invoiceList(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        model.addAttribute("entity_id", entity_id);

        ArrayList<invoice> invoices = new ArrayList<>();
        for (invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getInvoice_id().contains(entity_id)) {
                for (customer customer : customerRepository.findAll()) {
                    if(customer.getCustomer_id().equals(invoice.getCustomer_id()))
                        invoice.setCustomer_id(customer.getCustomer_name());
                }
                invoices.add(invoice);
            }
        }

        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceSize", invoices.size());

        return "pages/userPages/invoice";

    }

    @ModelAttribute("newInvoice")
    public InvoiceRegistrationDto newInvoice() {
        return new InvoiceRegistrationDto();
    }

    @GetMapping("{entity_id}/addInvoice")
    private String add_invoice(@PathVariable("entity_id") String entity_id, @ModelAttribute("invoice") InvoiceRegistrationDto
            invoiceRegistrationDto, Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        List<customer> allcustomers = (List) customerRepository.findAll();

        ArrayList<String> customers = new ArrayList<>();

        for (customer c : allcustomers) {
            if (c.getCustomer_id().contains(entity_id))
                customers.add(c.getCustomer_name());
        }

        List<account> allAccounts = (List) accountRepository.findAll();

        List<String> cashAccounts = new ArrayList<>();
        List<String> receivableAccounts = new ArrayList<>();
        ArrayList<String> payableAccounts = new ArrayList<>();
        ArrayList<String> earningAccounts = new ArrayList<>();
        List<String> terms = Arrays.asList(new String[]{"Due on Receipt", "Net 30 days", "Net 60 Days", "Net 90 Days"});
        for (account a : allAccounts) {
            if (a.getAccount_id().contains(entity_id)) {
                String name = a.getName();

                if (a.getRole().contains("cash") || (a.getRole().contains("asset") && a.getRole().contains("notes")))
                    cashAccounts.add(name);
                else if (name.contains("receivable") || name.contains("Receivable"))
                    receivableAccounts.add(name);
                else if ((name.contains("payable") || name.contains("Payable")) && Double.parseDouble(a.getCode()) < 3000.0)
                    payableAccounts.add(name);
                else if (a.getRole().startsWith("in") || (a.getRole().contains("capital")))
                    earningAccounts.add(name);
            }
        }

        model.addAttribute("customers", customers);
        model.addAttribute("terms", terms);
        model.addAttribute("cash_accounts", cashAccounts);
        model.addAttribute("receivable_accounts", receivableAccounts);
        model.addAttribute("payable_accounts", payableAccounts);
        model.addAttribute("earning_accounts", earningAccounts);
        model.addAttribute("entity_id", entity_id);
        return "pages/userPages/newInvoice";
    }

    @PostMapping("{entity_id}/addInvoice")
    private String add_invoice(@PathVariable("entity_id") String entity_id, @ModelAttribute("invoice") @Valid InvoiceRegistrationDto newInvoice, BindingResult result, Model model) {
        model.addAttribute("entity_id", entity_id);
        invoice newinvoice = new invoice();


        String customerName = newInvoice.getCustomer();
        List<customer> customers = (List) customerRepository.findAll();
        for (customer c : customers) {
            if (c.getCustomer_name().equals(customerName)) {
                newinvoice.setCustomer_id(c.getCustomer_id());
                break;
            }
        }

        newinvoice.setAmount_due(newInvoice.getAmount_due());
        Date date = new Date(System.currentTimeMillis());
        newinvoice.setCreated(date);
        newinvoice.setDate(date);
        newinvoice.setDue_date(newInvoice().getInvoice_date());
        newinvoice.setTerms(newInvoice.getTerm());
        newinvoice.setInvoice_number("Invoice_" + getAlphaNumericString(5));
        account account = accountRepository.findByName(newInvoice.getCash_account());
        if (account.getAccount_id().contains(entity_id))
            newinvoice.setCash_account_id(account.getAccount_id());
        account = accountRepository.findByName(newInvoice.getReceivable_account());
        if (account.getAccount_id().contains(entity_id))
            newinvoice.setReceivable_account_id(account.getAccount_id());
        account = accountRepository.findByName(newInvoice.getPayable_account());
        if (account.getAccount_id().contains(entity_id))
            newinvoice.setPayable_account_id(account.getAccount_id());
        account = accountRepository.findByName(newInvoice.getEarning_account());
        if (account.getAccount_id().contains(entity_id))
            newinvoice.setEarnings_account_id(account.getAccount_id());
        String id = entity_id + "_Invoice_" + getAlphaNumericString(5);
        newinvoice.setInvoice_id(id);

        ledger ledger = new ledger();
        String led_id = entity_id + getAlphaNumericString(5);
        ledger.setLedger_id(led_id);
        ledger.setCreated(date);
        ledger.setName("Invoice " + id);
        ledger.setEntity_id(entity_id);
        ledgerRepository.save(ledger);

        newinvoice.setLedger_id(led_id);


        if (newinvoice.getTerms().contains("Due")) {
            journalEntry journalEntry = new journalEntry();
            String jou_id = entity_id + "_" + getAlphaNumericString(5);
            journalEntry.setJe_id(jou_id);
            journalEntry.setDate(date);
            journalEntry.setCreated(date);
            journalEntry.setActivity("operating"); // needs editing
            journalEntry.setLedger_id(led_id);
            journalentryRepository.save(journalEntry);

            transaction transactionDebit = new transaction();
            transactionDebit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
            transactionDebit.setTx_type("debit");
            transactionDebit.setAccount_id(newinvoice.getEarnings_account_id());
            transactionDebit.setAmount(newinvoice.getAmount_due());
            transactionDebit.setJournal_entry_id(jou_id);
            transactionDebit.setCreated(date);

            transaction transactionCredit = new transaction();
            transactionCredit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
            transactionCredit.setTx_type("credit");
            transactionCredit.setAccount_id(newinvoice.getCash_account_id());
            transactionCredit.setAmount(newinvoice.getAmount_due());
            transactionCredit.setJournal_entry_id(jou_id);
            transactionCredit.setCreated(date);

            transactionRepository.save(transactionCredit);
            transactionRepository.save(transactionDebit);

            newinvoice.setAmount_paid(newinvoice.getAmount_due());
            newinvoice.setPaid(true);
        }

        invoiceRepository.save(newinvoice);

        model.addAttribute("entity_id", entity_id);
        return "redirect:/{entity_id}/invoice";
    }

    @GetMapping("{entity_id}/detailInvoice/{invoice_id}")
    private String detail_invoice(@PathVariable("entity_id") String entity_id, @PathVariable("invoice_id") String invoice_id,
                                  Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        invoice invoice = invoiceRepository.findById(invoice_id).get();
        customer customer = customerRepository.findById(invoice.getCustomer_id()).get();

        if (invoice.getAmount_paid() == null) {
            invoice.setAmount_paid(0.0);
            invoice.setAmount_unearned(invoice.getAmount_due());
        } else
            invoice.setAmount_unearned(invoice.getAmount_due() - invoice.getAmount_paid());
        invoice.setProgress(round((invoice.getAmount_paid() * 100) / invoice.getAmount_due(),2));

        Double credit = 0.0, debit = 0.0;

        ArrayList<transaction> transactions = new ArrayList<>();
        for (journalEntry journalEntry : journalentryRepository.findAll()) {
            if (journalEntry.getLedger_id().equals(invoice.getLedger_id())) {
                for (transaction transaction : transactionRepository.findAll()) {
                    if (transaction.getJournal_entry_id().equals(journalEntry.getJe_id())) {
                        account account = accountRepository.findById(transaction.getAccount_id()).get();
                        transaction.setAccount_id(account.getName());
                        if (transaction.getTx_type().equals("credit"))
                            credit = credit + transaction.getAmount();
                        else
                            debit = debit + transaction.getAmount();
                        transactions.add(transaction);
                    }
                }
            }
        }

        model.addAttribute("credit", credit);
        model.addAttribute("debit", debit);
        model.addAttribute("transactions", transactions);
        model.addAttribute("customer", customer);
        model.addAttribute("invoice", invoice);
        model.addAttribute("entity_id", entity_id);

        return "pages/userPages/invoiceDetail";
    }

    @GetMapping("{entity_id}/updateInvoice/{invoice_id}")
    private String update_invoice(@PathVariable("entity_id") String entity_id, @PathVariable("invoice_id") String invoice_id,
                                  Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************
        invoice invoice = invoiceRepository.findById(invoice_id).get();
        customer customer = customerRepository.findById(invoice.getCustomer_id()).get();


        model.addAttribute("customer", customer);
        model.addAttribute("invoice", invoice);
        model.addAttribute("terms", invoice.getTerms());
        model.addAttribute("account_cash", accountRepository.findById(invoice.getCash_account_id()).get().getName());
        model.addAttribute("amount_cash", invoice.getAmount_paid());
        model.addAttribute("account_receivable", accountRepository.findById(invoice.getReceivable_account_id()).get().getName());
        if (invoice.getAmount_paid() == null)
            invoice.setAmount_paid(0.0);
        model.addAttribute("amount_receivable", invoice.getAmount_due() - invoice.getAmount_paid());
        model.addAttribute("account_payable", accountRepository.findById(invoice.getPayable_account_id()).get().getName());
        model.addAttribute("amount_payable", 0);
        model.addAttribute("account_earning", accountRepository.findById(invoice.getEarnings_account_id()).get().getName());
        model.addAttribute("amount_earning", invoice.getAmount_paid());
        model.addAttribute("entity_id", entity_id);
        return "pages/userPages/invoiceUpdate";
    }

    @PostMapping("{entity_id}/updateInvoice/{invoice_id}")
    private String update_invoice(@ModelAttribute("invoice") BillRegistrationDto newInvoice, @PathVariable("entity_id") String entity_id, @PathVariable("invoice_id") String invoice_id,
                                  Model model, Principal principal) {

        ///****** this part is always logged user
        String firstName = userRepository.findByEmail(principal.getName()).getFirstName();
        String lastName = userRepository.findByEmail(principal.getName()).getLastName();
        String role = userRepository.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        ///******************
        Date date = new Date(System.currentTimeMillis());
        invoice updateInvoice = invoiceRepository.findById(invoice_id).get();
        Double amountPaid = updateInvoice.getAmount_paid();

        if (!transactionRepository.findById(entity_id + "_" + "AR").isPresent()) {
            transaction transaction = new transaction();
            transaction.setTransaction_id(entity_id + "_AR");
            transaction.setAmount(updateInvoice.getAmount_due());
            transaction.setDescription(entity_id + " Accounts Receivables");
            transaction.setAccount_id(updateInvoice.getReceivable_account_id());
            transaction.setCreated(date);
            transaction.setTx_type("notDefined");
            transaction.setJournal_entry_id("notDefined");
            transactionRepository.save(transaction);
        } else if (updateInvoice.getAmount_paid() == null || updateInvoice.getAmount_paid() == 0.0) {
            transaction transaction = transactionRepository.findById(entity_id + "_AR").get();
            transaction.setAmount(transaction.getAmount() + updateInvoice.getAmount_due());
            transactionRepository.save(transaction);
        }

        updateInvoice.setAmount_paid(newInvoice.getAmount_paid());
        updateInvoice.setUpdated(newInvoice.getBill_date());
        if(updateInvoice.getAmount_due() - updateInvoice.getAmount_paid() == 0.0)
            updateInvoice.setPaid(true);
        invoiceRepository.save(updateInvoice);
        journalEntry journalEntry = new journalEntry();
        String jou_id = entity_id + "_" + getAlphaNumericString(5);
        journalEntry.setJe_id(jou_id);
        journalEntry.setDate(newInvoice.getBill_date());
        journalEntry.setCreated(date);
        journalEntry.setActivity("operating"); // needs editing
        journalEntry.setLedger_id(updateInvoice.getLedger_id());
        journalentryRepository.save(journalEntry);


        transaction transactionCredit = new transaction();
        transactionCredit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
        transactionCredit.setTx_type("credit");
        transactionCredit.setAccount_id(updateInvoice.getCash_account_id());
        if (amountPaid == null)
            amountPaid = 0.0;
        transactionCredit.setAmount(updateInvoice.getAmount_paid() - amountPaid);
        transactionCredit.setJournal_entry_id(jou_id);
        transactionCredit.setCreated(date);

        transaction transactionDebit = new transaction();
        transactionDebit.setTransaction_id(entity_id + "_" + getAlphaNumericString(5));
        transactionDebit.setTx_type("debit");
        transactionDebit.setAccount_id(updateInvoice.getEarnings_account_id());
        transactionDebit.setAmount(updateInvoice.getAmount_paid() - amountPaid);
        transactionDebit.setJournal_entry_id(jou_id);
        transactionDebit.setCreated(date);

        transaction receivable = transactionRepository.findById(entity_id + "_AR").get();
        receivable.setAmount(receivable.getAmount() - (updateInvoice.getAmount_paid() - amountPaid));
        transactionRepository.save(receivable);

        transactionRepository.save(transactionCredit);
        transactionRepository.save(transactionDebit);

        model.addAttribute("entity_id", entity_id);
        return "redirect:/{entity_id}/invoice";
    }

    @GetMapping("{entity_id}/deleteInvoice/{invoice_id}")
    private String delete_invoice( @PathVariable("entity_id") String entity_id, @PathVariable("invoice_id") String invoice_id,
                                  Model model, Principal principal) {

        ///****** this part is always logged user
        String firstName = userRepository.findByEmail(principal.getName()).getFirstName();
        String lastName = userRepository.findByEmail(principal.getName()).getLastName();
        String role = userRepository.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        ///******************

        invoice invoice = invoiceRepository.findById(invoice_id).get();
        Double paid = 0.0, unpaid = 0.0, due = 0.0;
        paid = invoice.getAmount_paid(); due = invoice.getAmount_due(); unpaid = due - paid;
        String ledger_id = invoice.getLedger_id();
        ledger ledger = ledgerRepository.findById(invoice.getLedger_id()).get();


        for (journalEntry journalEntry: journalentryRepository.findAll()){
            if( journalEntry.getLedger_id().equals(ledger_id)) {
                for (transaction transaction: transactionRepository.findAll()){
                    if(transaction.getJournal_entry_id().equals(journalEntry.getJe_id()))
                        transactionRepository.delete(transaction);
                }
                journalentryRepository.delete(journalEntry);
            }
        }

        if(unpaid != 0){
            transaction transaction = transactionRepository.findById(entity_id+"_AR").get();
            transaction.setAmount(transaction.getAmount() - unpaid);
            transactionRepository.save(transaction);
        }



        invoiceRepository.delete(invoice);
        ledgerRepository.delete(ledger);
        model.addAttribute("entity_id",entity_id);

        return "redirect:/{entity_id}/invoice";
    }


    /*******************************************************************************************

     Chart of Accounts


     *//////////////////////////////////////////////////////////////////////////////////
    @GetMapping("{entity_id}/coa")
    private String coa(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        ArrayList<account> accounts = new ArrayList<>();
        for (account account : accountRepository.findAll()) {
            if (account.getAccount_id().contains(entity_id))
                accounts.add(account);
        }

        model.addAttribute("entity_id", entity_id);
        model.addAttribute("accounts", accounts);
        return "pages/userPages/coa";
    }

    @ModelAttribute("account")
    public account newAccount() {
        return new account();
    }

    @GetMapping("/{entity_id}/addAccount")
    private String add(@PathVariable("entity_id") String entity_id, @ModelAttribute("account") account
            account, Model model, BindingResult result, Principal principal) {

        ///****** this part is always logged in user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        model.addAttribute("entity_id", entity_id);
        return "pages/userPages/newAccount";
    }

    @PostMapping("/{entity_id}/addAccount")
    private String add1(@PathVariable("entity_id") String entity_id, @ModelAttribute("account") account account,
                        BindingResult result) {

        Date date = new Date(System.currentTimeMillis());
        account.setCreated(date);
        account.setAccount_id(entity_id + "_" + getAlphaNumericString(5));
        accountRepository.save(account);
        return "redirect:/{entity_id}/coa";
    }

    /*******************************************************************************************
                    Ledger

     *////////////////////////////////////////////////////////////////////////////////////////////


    @Autowired
    private LedgerRepository ledgerRepository;

    @GetMapping("{entity_id}/ledger")
    private String ledgers(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        ArrayList<ledger> ledgers = new ArrayList<>();
        for (ledger ledger : ledgerRepository.findAll()) {
            if (ledger.getEntity_id().equals(entity_id))
                ledgers.add(ledger);
        }
        model.addAttribute("entity_id", entity_id);
        model.addAttribute("ledgers", ledgers);
        return "pages/userPages/ledger";

    }

    /****************************************************
     *
     *       Journal ledger
     */

    @Autowired
    JournalentryRepository journalentryRepository;

    @GetMapping("{entity_id}/journalEntry/{ledger_id}")
    private String ledgersJE(@PathVariable("entity_id") String entity_id, @PathVariable("ledger_id") String ledger_id,
                             Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        ArrayList<journalEntry> journalEntrys = new ArrayList<journalEntry>();

        for (journalEntry journalEntry : journalentryRepository.findAll()) {
            if (journalEntry.getLedger_id().equals(ledger_id))
                journalEntrys.add(journalEntry);
        }


        model.addAttribute("entity_id", entity_id);
        model.addAttribute("ledger_id", ledger_id);
        model.addAttribute("jes", journalEntrys);


        return "pages/userPages/journalEntry";

    }

    /*********
     *
     * transactions
     */

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("{entity_id}/ledger/{je_id}")
    private String JEsTransactions(@PathVariable("entity_id") String entity_id, @PathVariable("je_id") String je_id,
                                   Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        ///******************

        ArrayList<transaction> transactions = new ArrayList<transaction>();

        for (transaction transaction : transactionRepository.findAll()) {
            if (transaction.getJournal_entry_id().equals(je_id)) {
                String account = accountRepository.findById(transaction.getAccount_id()).get().getName();
                transaction.setAccount_id(account);
                transactions.add(transaction);
            }
        }

        model.addAttribute("entity_id", entity_id);
        model.addAttribute("je_id", je_id);
        model.addAttribute("transactions", transactions);
        return "pages/userPages/transaction";
    }

    /*

                Reporting

     */

    @ModelAttribute("report")
    public reportdates newReport() {
        return new reportdates();
    }

    @Autowired
    EntityRepository entityRepository;

    @GetMapping("{entity_id}/report")
    private String reporting(@PathVariable("entity_id") String entity_id, @ModelAttribute("report") reportdates reportdates,
                             Model model, Principal principal) {

        ///****** this part is always logged user
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }

        model.addAttribute("report", reportdates);

        model.addAttribute("entity_id", entity_id);
        //return "pages/userPages/financialPosition";
        return "pages/userPages/report";
    }

    @PostMapping("{entity_id}/report")
    private String add_report(@PathVariable("entity_id") String entity_id, @ModelAttribute("report") reportdates reportdates,
                              Model model, Principal principal) {
        LocalDate sd = reportdates.getDate_start().toLocalDate();
        LocalDate ed = reportdates.getDate_end().toLocalDate();

        Date endDate = new Date(System.currentTimeMillis());
        int startYear = endDate.toLocalDate().getYear() + 1;
        int startMonth = endDate.toLocalDate().getMonthValue();
        int startDay = endDate.toLocalDate().getDayOfMonth();
        LocalDate Edate = ed;
        LocalDate a = sd;
        LocalDate Sdate = endDate.toLocalDate();
        Date end = java.sql.Date.valueOf(Edate);
        reports(model, a, Edate, entity_id);
        model.addAttribute("entity_id", entity_id);

        String entity_name = "";
        for (entity entity : entityRepository.findAll()) {
            if (entity.getEntity_id().equals(entity_id))
                entity_name = entity.getName();
        }

        model.addAttribute("entity_name", entity_name);
        model.addAttribute("Sdate", Sdate);
        model.addAttribute("Edate", end);
        return "pages/userPages/financialPosition";
    }



    public void reports(Model model, LocalDate Sdate, LocalDate Edate, String entity_id) {
        ArrayList<transaction> filteredTransactions = new ArrayList<>();
        ArrayList<account> transactionAccounts = new ArrayList<>();
        ArrayList<trans> accBalance = new ArrayList<>();
        ArrayList<String> PPe = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        Double totalRevenue = 0.0, totoalExpense = 0.0;
        ArrayList<String> expense = new ArrayList<>();


        for (transaction transaction : transactionRepository.findAll()) {
            if (transaction.getTransaction_id().startsWith(entity_id)) {
                    filteredTransactions.add(transaction);
            }
        }

        ArrayList<bill> bils = new ArrayList<>();
        for (bill bill: billRepository.findAll() ){
            if (bill.getBill_id().startsWith(entity_id)){
                bils.add(bill);
            }
        }

        ArrayList<invoice> invoices = new ArrayList<>();
        for (invoice invoice: invoiceRepository.findAll() ){
            if (invoice.getInvoice_id().startsWith(entity_id)){
                invoices.add(invoice);
            }
        }

        LocalDate prevStart, prevEnd;
        int day = Sdate.getDayOfMonth();
        int mon = Sdate.getMonthValue();
        int year = Sdate.getYear();

        int daye = Edate.getDayOfMonth();
        int mone = Edate.getMonthValue();
        int yeare = Edate.getYear();

        prevStart = LocalDate.of(year - 1 , mon, day);
        prevEnd = LocalDate.of(yeare - 1 , mone, daye);

        repo prev = perYear(filteredTransactions, prevStart, prevEnd, bils, invoices);
        repo curr = perYear(filteredTransactions, Sdate, Edate, bils, invoices);
        curr.getFp().setCCE(curr.getFp().getCCE() + prev.getFp().getCCE());

        curr.getFp().setInve(curr.getFp().getInve() + prev.getFp().getInve());
        curr.getFp().setTOR(curr.getFp().getTOR() + prev.getFp().getTOR());
        Double cainc = prev.getFp().getInve() + prev.getFp().getCCE() + prev.getFp().getTOR();
        curr.getFp().setCurrasset(curr.getFp().getCurrasset() + cainc);
        Double ncainc = prev.getFp().getInvestA() + prev.getFp().getPPE() + prev.getFp().getIA();
        curr.getFp().setInvestA(curr.getFp().getInvestA() + prev.getFp().getInvestA());
        curr.getFp().setPPE(curr.getFp().getPPE() + prev.getFp().getPPE());
        curr.getFp().setIA(curr.getFp().getIA() + prev.getFp().getIA());
        curr.getFp().setNoncurrasset(curr.getFp().getNoncurrasset() + ncainc);
        curr.getFp().setAsset(curr.getFp().getAsset() + ncainc + cainc);
        curr.getFp().setTP(curr.getFp().getTP() + prev.getFp().getTP());
        Double clinc = prev.getFp().getTP();
        curr.getFp().setCurrliab(curr.getFp().getCurrliab() + clinc);
        Double nclinc = prev.getFp().getBL() + prev.getFp().getLTEP();
        curr.getFp().setBL(curr.getFp().getBL() + prev.getFp().getBL());
        curr.getFp().setLTEP(curr.getFp().getLTEP() + prev.getFp().getLTEP());
        curr.getFp().setNoncurrliab(curr.getFp().getNoncurrliab() + nclinc);
        curr.getFp().setLiab(curr.getFp().getLiab() + nclinc + clinc);
        curr.getFp().setRE(curr.getFp().getRE() + prev.getFp().getRE());
        curr.getFp().setSC(curr.getFp().getAsset() - ( curr.getFp().getRE() + curr.getFp().getLiab()));
        curr.getFp().setOther(curr.getFp().getSC() + curr.getFp().getRE() );

        Double x= (curr.getFp().getOther() - curr.getFp().getSC()) - curr.getCe().getNetIncome();

        curr.getCe().setSC(curr.getFp().getOther() - (x +  curr.getCe().getNetIncome()));
        curr.getCe().setRE(prev.getCe().getRE() + curr.getCe().getRE());
        System.out.println(curr.getCe().getSC());
        System.out.println(curr.getCe().getRE());
        curr.getCe().setTotal( curr.getCe().getSC() + curr.getCe().getRE());

        curr.getCf().setCash_receipts_from_customers(curr.getCf().getCash_receipts_from_customers() +
                (prev.getFp().getTOR() - curr.getFp().getTOR()) );

        curr.getCf().setPayments_to_suppliers_of_goods(curr.getCf().getPayments_to_suppliers_of_services() + (prev.getFp().getTP() -
                curr.getFp().getTP() ));

        curr.getCf().setDecrease_increase_in_trade_and_other_receivables(curr.getFp().getTOR() - prev.getFp().getTOR());

        curr.getCf().setDecrease_increase_in_inventories( curr.getFp().getInve() - prev.getFp().getInve());

        curr.getCf().setIncrease_decrease_in_trade_payables(curr.getFp().getTP() -  prev.getFp().getTP());

        curr.getCf().setIncrease_decrease_in_current_and_long_term_employee_benefit_payable(
                curr.getCf().getIncrease_decrease_in_current_and_long_term_employee_benefit_payable() -
                        prev.getCf().getIncrease_decrease_in_current_and_long_term_employee_benefit_payable()
        );
        curr.getCf().setNet_cash_from_operating_activities(
                curr.getCf().getNet_cash_from_operating_activities() +
                        curr.getCf().getCash_receipts_from_customers() +
                        curr.getCf().getPayments_to_suppliers_of_goods()+
                        curr.getCf().getDecrease_increase_in_trade_and_other_receivables() +
                        curr.getCf().getDecrease_increase_in_inventories()+
                        curr.getCf().getIncrease_decrease_in_trade_payables()+
                        curr.getCf().getIncrease_decrease_in_current_and_long_term_employee_benefit_payable()
        );

        curr.getCf().setNet_increase_decrease_in_cash_and_cash_equivalents(
                curr.getCf().getNet_cash_from_operating_activities() +
                        curr.getCf().getNet_cash_used_in_financing_activities()+
                        curr.getCf().getNet_cash_used_in_investing_activities()
        );

        curr.getCf().setCash_and_cash_equivalents_at_beginning_of_year(prev.getCf().getCash_and_cash_equivalents_at_end_of_year());
        curr.getCf().setCash_and_cash_equivalents_at_end_of_year(curr.getCf().getCash_and_cash_equivalents_at_beginning_of_year() +
                curr.getCf().getNet_increase_decrease_in_cash_and_cash_equivalents());


        model.addAttribute("fpprev", prev.fp);
        model.addAttribute("ieprev", prev.ie);
        model.addAttribute("ceprev", prev.ce);
        model.addAttribute("cfprev", prev.cf);
        model.addAttribute("fpcurr", curr.fp);
        model.addAttribute("iecurr", curr.ie);
        model.addAttribute("cecurr", curr.ce);
        model.addAttribute("cfcurr", curr.cf);
    }

    public repo perYear(ArrayList<transaction> filteredTransaction, LocalDate Sdate, LocalDate Edate, ArrayList<bill> bils, ArrayList<invoice> invoices ) {

        ArrayList<transaction> filteredTransactions = new ArrayList<>();
        ArrayList<account> transactionAccounts = new ArrayList<>();
        ArrayList<trans> accBalance = new ArrayList<>();
        ArrayList<String> PPe = new ArrayList<>();
        ArrayList<String> income = new ArrayList<>();
        Double totalRevenue = 0.0, totoalExpense = 0.0;
        ArrayList<String> expense = new ArrayList<>();

        for (transaction transaction : filteredTransaction) {
            if ((transaction.getCreated().toLocalDate().isAfter(Sdate) || transaction.getCreated().toLocalDate().isEqual(Sdate)) &&
                    (transaction.getCreated().toLocalDate().isBefore(Edate) || transaction.getCreated().toLocalDate().isEqual(Edate))) {
                account account = accountRepository.findById(transaction.getAccount_id()).get();
                if (account.getRole().startsWith("ex_") || (account.getRole().contains("_ppe") && !account.getBalance_type().equals("credit"))) {
                    if ((account.getName().contains("Utilities") ||
                            account.getName().contains("taxes") || account.getRole().contains("tax") || account.getName().contains("Insurance") ||
                            account.getName().contains("Salaries") || account.getRole().contains("ltep")))
                        PPe.add(account.getAccount_id());
                    totoalExpense = totoalExpense + transaction.getAmount();
                    expense.add(transaction.getAccount_id());
                } else if (account.getRole().startsWith("in_")) {
                    totalRevenue = totalRevenue + transaction.getAmount();
                    income.add(transaction.getAccount_id());
                }
                if (transaction.getTx_type().equals("debit")){

                    transaction.setAmount(transaction.getAmount() * -1);

                }
                if (checkAccount(accBalance, transaction.getAccount_id())) {

                    for (trans trans : accBalance) {
                        if (trans.getAccount_id().equals(transaction.getAccount_id())) {
                            trans.setAccount_balance(trans.getAccount_balance() + transaction.getAmount());
                        }
                    }
                } else {
                    transactionAccounts.add(accountRepository.findById(transaction.getAccount_id()).get());
                    accBalance.add(new trans(transaction.getAccount_id(), transaction.getAmount()));
                }
                filteredTransactions.add(transaction);
            }

        }

        //  SoCI
        Double misExpense = 0.0, rent = 0.0, gas = 0.0, electricity = 0.0;
        Double BankCharge = 0.0, gift = 0.0, mealEnt = 0.0;
        Double utility = 0.0, officeExpense = 0.0, commission = 0.0, auto = 0.0, insurance = 0.0, license = 0.0;

        Double revenue = 0.0;
        Double costOfGoodsSold = 0.0;
        Double operatingExpense = 0.0;
        Double advertsingExpense = 0.0;
        Double frieghtCost = 0.0;
        Double maintainanceCost = 0.0;
        Double employeeExpense = 0.0;
        Double otherOperatingExpense = 0.0;
        Double depreciationAmortization = 0.0;
        Double otherExpense = 0.0;
        Double interestExpense = 0.0;
        Double profitBeforeTax = 0.0;
        Double incomeTax = 0.0;
        Double profitForYear = 0.0;
        Double unrealized = 0.0;
        Double availableforsale = 0.0;
        Double amortization = 0.0;
        for(trans trans: accBalance){
            account account = accountRepository.findById(trans.account_id).get();
            if (account.getRole().startsWith("in_sales")){
                revenue = revenue + (trans.getAccount_balance() * -1);
            }
            else if (account.getRole().startsWith("ex_")){
                if(account.getName().contains("Electricity") || account.getName().contains("rent")
                        || account.getName().contains("Gas")){
                    if (account.getName().contains("rent")){
                        rent = rent + trans.getAccount_balance();
                    }
                    utility = utility + (trans.getAccount_balance() );
                }
                else if (account.getRole().contains("other") || account.getName().contains("Bank") ||
                        account.getName().contains("Gift") || account.getName().contains("Meal")){
                    otherExpense = otherExpense + (trans.getAccount_balance() );
                }
                else if(account.getName().contains("Amortization") || account.getName().contains("Less")){
                    if (account.getName().contains("Amortization"))
                        amortization = amortization + trans.getAccount_balance();
                    depreciationAmortization = depreciationAmortization + (trans.getAccount_balance() );
                }
                else if (account.getName().contains("office") || account.getName().contains("Commission")
                        || account.getName().contains("Auto") || account.getName().contains("Insurance") || account.getName().contains("License")){
                    otherOperatingExpense = otherOperatingExpense + (trans.getAccount_balance() );
                }
                else if (account.getRole().contains("interest")){
                    interestExpense = interestExpense + (trans.getAccount_balance() );
                }
                else if (account.getName().contains("Salaries")){
                    employeeExpense = employeeExpense + (trans.getAccount_balance());
                }
                else if (account.getName().contains("Maintenance")){
                    maintainanceCost = maintainanceCost + (trans.getAccount_balance() );
                }
                else if (account.getName().contains("Advertising")){
                    advertsingExpense = advertsingExpense + (trans.getAccount_balance() );
                }
                else if (account.getName().contains("freight")){
                    frieghtCost = frieghtCost + (trans.getAccount_balance() );
                }
                else if (account.getName().contains("Unrealized")){
                    unrealized = unrealized + (trans.getAccount_balance() );
                }
            }
        }

        costOfGoodsSold = revenue * 0.8;
        otherOperatingExpense = otherOperatingExpense + utility;
        operatingExpense = advertsingExpense + frieghtCost + maintainanceCost + employeeExpense + otherOperatingExpense;

        profitBeforeTax = revenue - (costOfGoodsSold + operatingExpense + depreciationAmortization + otherExpense + interestExpense);

        Double taxrate = 0.0;
        if(profitBeforeTax < 7201){
            taxrate = 0.0;
        }
        else if (profitBeforeTax < 19401){
            taxrate = 0.1;
        }
        else if (profitBeforeTax < 38401){
            taxrate = 0.15;
        }
        else if (profitBeforeTax < 63601){
            taxrate = 0.2;
        }
        else if (profitBeforeTax < 93601){
            taxrate = 0.25;
        }
        else if (profitBeforeTax < 130801){
            taxrate = 0.3;
        }
        else {
            taxrate = 0.35;
        }

        if (profitBeforeTax < 1)
            incomeTax = 0.0;
        else
            incomeTax = profitBeforeTax * taxrate;

        profitForYear = profitBeforeTax - incomeTax;


        ie ie = new ie();
        ie.setCostOfGoodsSold(costOfGoodsSold); ie.setAdvertsingExpense(advertsingExpense);
        if(revenue > 0)
            ie.setRevenue(revenue);
        else
            ie.setRevenue(revenue * -1);

        ie.setFrieghtCost(frieghtCost); ie.setMaintainanceCost(maintainanceCost);
        ie.setEmployeeExpense(employeeExpense); ie.setOtherOperatingExpense(otherOperatingExpense);
        ie.setDepreciationAmortization(depreciationAmortization); ie.setOtherExpense(otherExpense); ie.setInterestExpense(interestExpense);
        ie.setProfitBeforeTax(profitBeforeTax); ie.setIncomeTax(incomeTax); ie.setProfitForYear(profitForYear);

        ////// SoCE

        Double netIncome = profitForYear;
        Double dividents = 0.0;
        Double issues = 0.0;
        Double repurchase = 0.0;

        /// SoFP
        Double asset = 0.0, currasset = 0.0, noncurrasset = 0.0;
        Double liab = 0.0, currliab = 0.0, noncurrliab = 0.0, other = 0.0;
        Double PPE = 0.0, CCE = 0.0, Inve = 0.0, TOR = 0.0, IA = 0.0, DTA = 0.0, ppE = 0.0, InvestA = 0.0;
        Double TP = 0.0, IP = 0.0, CTP = 0.0, STEP = 0.0, PWO = 0.0;
        Double  LTEP = 0.0, BL = 0.0 ;
        Double SC = 0.0, RE = 0.0;

        // SoCF

        Double gain = 0.0;
        for (trans trans : accBalance) {

            account account = accountRepository.findById(trans.account_id).get();
            if (account.getRole().contains("cash")) {
                CCE = CCE + trans.getAccount_balance();
            }
            else if (account.getRole().contains("_recv"))
                TOR = TOR + trans.getAccount_balance();
            else if ((account.getRole().contains("_ppe") && !account.getName().contains("Less")) ||
                    (account.getName().contains("Property") && account.getName().contains(" Sale"))) {
                PPE = PPE + trans.getAccount_balance();

            } else if (account.getRole().contains("sales") || account.getName().contains("Supplies")) {
                if (account.getName().contains("Supplies")) {
                    Inve = Inve + trans.getAccount_balance();
                } else{
                    Inve = Inve + trans.getAccount_balance() * 0.8;
                    PWO = PWO + (trans.getAccount_balance() * 0.02 * -1);
                }
            } else if (account.getRole().startsWith("lia_cl_acc")) {
                TP = TP + trans.getAccount_balance();
            } else if (PPe.contains(account.getAccount_id())) {
                ppE = ppE + trans.getAccount_balance();
            }
            else if (account.getRole().contains("_ltep")){
                LTEP = LTEP + trans.getAccount_balance();
            }
            else if (account.getName().contains("Investment")){
                InvestA = InvestA + trans.getAccount_balance();
            }
            else if (account.getRole().endsWith("_ia")){
                IA = IA + trans.getAccount_balance();
            }
            else if (account.getRole().contains("_wage")) {
                STEP = STEP + (trans.getAccount_balance() * -1);
            }
            else if (account.getRole().contains("loan")){
                BL = BL + (trans.getAccount_balance() * -1);
            }
            else if (account.getName().contains("Divident")){
                dividents = dividents + (trans.getAccount_balance());
            }
            else if (account.getName().contains("Issue")){
                issues = issues + (trans.getAccount_balance());
            }
            else if (account.getName().contains("Gain")){
                gain = gain + (trans.getAccount_balance());
            }
        }

        for (bill bill : bils) {
            if ((bill.getCreated().toLocalDate().isAfter(Sdate) || bill.getCreated().toLocalDate().isEqual(Sdate)) &&
                    (bill.getCreated().toLocalDate().isBefore(Edate) || bill.getCreated().toLocalDate().isEqual(Edate))) {
                if (bill.getPayable_account_id().equals("lia_cl_acc_pay"))
                    TP = TP + (bill.getAmount_due() - bill.getAmount_paid());
            }
        }
        for (invoice invoice : invoices) {
            if ((invoice.getCreated().toLocalDate().isAfter(Sdate) || invoice.getCreated().toLocalDate().isEqual(Sdate)) &&
                    (invoice.getCreated().toLocalDate().isBefore(Edate) || invoice.getCreated().toLocalDate().isEqual(Edate))) {
                if (invoice.getReceivable_account_id().equals("lia_cl_acc_recv"))
                    TOR = TOR + (invoice.getAmount_due() - invoice.getAmount_paid());
            }
        }

        // calculations

 //       TP = (((TP - BL) - STEP) - LTEP);

        RE = totalRevenue - totoalExpense;


        currasset = CCE + TOR + ppE + Inve;

        availableforsale = Inve + (Inve * 0.2);
        ie.setAvailableforsale(availableforsale);
        Double totalIncome = profitForYear + availableforsale + unrealized;
        ie.setTotalIncome(totalIncome);

        noncurrasset = PPE + IA + InvestA + IA;
        asset = currasset + noncurrasset;
        currliab = CTP + IP + DTA + TP + STEP + PWO;
        noncurrliab = LTEP + BL;
        liab = currliab + noncurrliab;
        SC = (asset - liab) - RE;
        other = SC + RE;

        fp fp = new fp();

        fp.setCCE(CCE); fp.setTOR(TOR); fp.setPpE(ppE); fp.setInve(Inve);
        fp.setPPE(PPE); fp.setIA(IA); fp.setInvestA(InvestA);
        fp.setCTP(CTP); fp.setIP(IP); fp.setDTA(DTA); fp.setTP(TP); fp.setSTEP(STEP); fp.setPWO(PWO);
        fp.setLTEP(LTEP); fp.setBL(BL);
        fp.setSC(SC); fp.setRE(RE);
        fp.setCurrasset(currasset); fp.setNoncurrasset(noncurrasset); fp.setAsset(asset);
        fp.setCurrliab(currliab); fp.setNoncurrliab(noncurrliab); fp.setLiab(liab);
        fp.setOther(other);

        ce ce = new ce();

        dividents = dividents;

        ce.setDividents(dividents);
        ce.setIssues(issues);

        ce.setRE(netIncome - dividents);
        ce.setNetIncome(netIncome);
        Double x= (other - SC) - netIncome;
        ce.setSC(SC + x);
        ce.setTotal(ce.getSC() + (netIncome - dividents) );



        /*
                SoCF
         */
        cf cf = new cf();

        Double Profit_for_the_year  = 0.0;
        Double Amortisation  = 0.0;
        Double Depreciation  = 0.0;
        Double Cash_receipts_from_customers  = 0.0;
        Double Payments_to_employees  = 0.0;
        Double Payments_to_suppliers_of_services  = 0.0;
        Double Payments_to_suppliers_of_goods  = 0.0;
        Double Interest_paid  = 0.0;
        Double Income_taxes_paid  = 0.0;
        Double Decrease_increase_in_trade_and_other_receivables  = 0.0;
        Double Decrease_increase_in_inventories  = 0.0;
        Double Increase_decrease_in_trade_payables  = 0.0;
        Double Increase_decrease_in_current_and_long_term_employee_benefit_payable  = 0.0;
        Double Gain_on_sale_of_equipment  = 0.0;
        Double Net_cash_from_operating_activities  = 0.0;

        Double Proceeds_from_sale_of_equipment  = 0.0;
        Double Purchases_of_equipment  = 0.0;
        Double Net_cash_used_in_investing_activities  = 0.0;

        Double Payment_of_finance_lease_liabilities  = 0.0;
        Double Repayment_of_borrowings  = 0.0;
        Double Dividends_paid  = 0.0;
        Double Net_cash_used_in_financing_activities  = 0.0;


        Double Net_increase_decrease_in_cash_and_cash_equivalents  = 0.0;
        Double Cash_and_cash_equivalents_at_beginning_of_year  = 0.0;
        Double Cash_and_cash_equivalents_at_end_of_year  = 0.0;


        Profit_for_the_year = profitForYear;
        Amortisation = amortization;
        Depreciation = depreciationAmortization - amortization;
        Cash_receipts_from_customers = revenue - TOR;
        Payments_to_employees = employeeExpense;
        Payments_to_suppliers_of_goods = costOfGoodsSold - TP;

        Gain_on_sale_of_equipment = gain;
        Decrease_increase_in_trade_and_other_receivables  = TOR;
        Decrease_increase_in_inventories = Inve;
        Increase_decrease_in_trade_payables = TP;
        Increase_decrease_in_current_and_long_term_employee_benefit_payable = STEP + LTEP;
        Net_cash_from_operating_activities = Profit_for_the_year + Amortisation + Depreciation + Cash_receipts_from_customers +
                Payments_to_employees + Payments_to_suppliers_of_goods + Gain_on_sale_of_equipment +
                Decrease_increase_in_trade_and_other_receivables  + Decrease_increase_in_inventories +
                Increase_decrease_in_trade_payables + Increase_decrease_in_current_and_long_term_employee_benefit_payable;
        Proceeds_from_sale_of_equipment = totalRevenue - revenue;
        Purchases_of_equipment = PPE;
        Net_cash_used_in_investing_activities = Proceeds_from_sale_of_equipment + Purchases_of_equipment;

        Payment_of_finance_lease_liabilities = rent;
        Repayment_of_borrowings = 0.0;
        Dividends_paid = dividents;

        Net_cash_used_in_financing_activities = Payment_of_finance_lease_liabilities + Repayment_of_borrowings + Dividends_paid;

        Net_increase_decrease_in_cash_and_cash_equivalents = Net_cash_used_in_financing_activities + Net_cash_from_operating_activities + Net_cash_used_in_investing_activities;

        Cash_and_cash_equivalents_at_beginning_of_year = CCE;
        Cash_and_cash_equivalents_at_end_of_year = Net_increase_decrease_in_cash_and_cash_equivalents + Cash_and_cash_equivalents_at_beginning_of_year;

        cf.setAmortisation(Amortisation); cf.setProfit_for_the_year(Profit_for_the_year); cf.setDepreciation(Depreciation);
        cf.setCash_receipts_from_customers(Cash_receipts_from_customers);
        cf.setPayments_to_employees(Payments_to_employees);
        cf.setPayments_to_suppliers_of_goods(Payments_to_suppliers_of_goods);
        cf.setGain_on_sale_of_equipment(Gain_on_sale_of_equipment);
        cf.setDecrease_increase_in_trade_and_other_receivables(Decrease_increase_in_trade_and_other_receivables);
        cf.setDecrease_increase_in_inventories(Decrease_increase_in_inventories);
        cf.setIncrease_decrease_in_trade_payables(Increase_decrease_in_trade_payables);
        cf.setIncrease_decrease_in_current_and_long_term_employee_benefit_payable(Increase_decrease_in_current_and_long_term_employee_benefit_payable);
        cf.setNet_cash_from_operating_activities(Net_cash_from_operating_activities);
        cf.setProceeds_from_sale_of_equipment(Proceeds_from_sale_of_equipment);
        cf.setPurchases_of_equipment(Purchases_of_equipment);
        cf.setNet_cash_used_in_investing_activities(Net_cash_used_in_investing_activities);
        cf.setPayment_of_finance_lease_liabilities(Payment_of_finance_lease_liabilities);
        cf.setRepayment_of_borrowings(Repayment_of_borrowings);
        cf.setDividends_paid(Dividends_paid);
        cf.setNet_cash_used_in_financing_activities(Net_cash_used_in_financing_activities);
        cf.setNet_increase_decrease_in_cash_and_cash_equivalents(Net_increase_decrease_in_cash_and_cash_equivalents);
        cf.setCash_and_cash_equivalents_at_end_of_year(Cash_and_cash_equivalents_at_end_of_year);
        cf.setCash_and_cash_equivalents_at_beginning_of_year(Cash_and_cash_equivalents_at_beginning_of_year);


        repo r = new repo();
        r.setFp(fp); r.setIe(ie); r.setCe(ce); r.setCf(cf);
        return r;
    }

    boolean checkAccount(ArrayList<trans> trans, String acc) {
        for (trans trans1 : trans) {
            if (trans1.getAccount_id().equals(acc))
                return true;
        }
        return false;
    }

    public String DoubletoString (Double a){
        if (a > 0.0)
            return a.toString();

        return "("+(a * -1)+")";
    }

}

class trans {
    String account_id;
    Double account_balance;

    public trans() {
    }

    public trans(String account_id, Double account_balance) {
        this.account_id = account_id;
        this.account_balance = account_balance;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public Double getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(Double account_balance) {
        this.account_balance = account_balance;
    }
}

class fp {
    Double asset = 0.0, currasset = 0.0, noncurrasset = 0.0;
    Double liab = 0.0, currliab = 0.0, noncurrliab = 0.0, other = 0.0;
    Double PPE = 0.0, CCE = 0.0, Inve = 0.0, TOR = 0.0, IA = 0.0, DTA = 0.0, ppE = 0.0, InvestA = 0.0;
    Double TP = 0.0, IP = 0.0, CTP = 0.0, STEP = 0.0, PWO = 0.0;
    Double LTLP = 0.0, LTEP = 0.0, BL = 0.0;
    Double SC = 0.0, RE = 0.0;


    public fp() {
    }

    public fp(Double BL, Double PWO, Double InvestA, Double asset, Double currasset, Double noncurrasset, Double liab, Double currliab, Double noncurrliab, Double other, Double PPE, Double CCE, Double inve, Double TOR, Double IA, Double DTA, Double ppE, Double TP, Double IP, Double CTP, Double STEP, Double LTLP, Double LTEP, Double SC, Double RE) {
        this.BL = BL;
        this.PWO = PWO;
        this.InvestA = InvestA;
        this.asset = asset;
        this.currasset = currasset;
        this.noncurrasset = noncurrasset;
        this.liab = liab;
        this.currliab = currliab;
        this.noncurrliab = noncurrliab;
        this.other = other;
        this.PPE = PPE;
        this.CCE = CCE;
        Inve = inve;
        this.TOR = TOR;
        this.IA = IA;
        this.DTA = DTA;
        this.ppE = ppE;
        this.TP = TP;
        this.IP = IP;
        this.CTP = CTP;
        this.STEP = STEP;
        this.LTLP = LTLP;
        this.LTEP = LTEP;
        this.SC = SC;
        this.RE = RE;
    }

    public Double getPWO() {
        return PWO;
    }

    public Double getBL() {
        return BL;
    }

    public void setBL(Double BL) {
        this.BL = BL;
    }

    public void setPWO(Double PWO) {
        this.PWO = PWO;
    }

    public Double getInvestA() {
        return InvestA;
    }

    public void setInvestA(Double investA) {
        InvestA = investA;
    }

    public Double getAsset() {
        return asset;
    }

    public void setAsset(Double asset) {
        this.asset = asset;
    }

    public Double getCurrasset() {
        return currasset;
    }

    public void setCurrasset(Double currasset) {
        this.currasset = currasset;
    }

    public Double getNoncurrasset() {
        return noncurrasset;
    }

    public void setNoncurrasset(Double noncurrasset) {
        this.noncurrasset = noncurrasset;
    }

    public Double getLiab() {
        return liab;
    }

    public void setLiab(Double liab) {
        this.liab = liab;
    }

    public Double getCurrliab() {
        return currliab;
    }

    public void setCurrliab(Double currliab) {
        this.currliab = currliab;
    }

    public Double getNoncurrliab() {
        return noncurrliab;
    }

    public void setNoncurrliab(Double noncurrliab) {
        this.noncurrliab = noncurrliab;
    }

    public Double getOther() {
        return other;
    }

    public void setOther(Double other) {
        this.other = other;
    }

    public Double getPPE() {
        return PPE;
    }

    public void setPPE(Double PPE) {
        this.PPE = PPE;
    }

    public Double getCCE() {
        return CCE;
    }

    public void setCCE(Double CCE) {
        this.CCE = CCE;
    }

    public Double getInve() {
        return Inve;
    }

    public void setInve(Double inve) {
        Inve = inve;
    }

    public Double getTOR() {
        return TOR;
    }

    public void setTOR(Double TOR) {
        this.TOR = TOR;
    }

    public Double getIA() {
        return IA;
    }

    public void setIA(Double IA) {
        this.IA = IA;
    }

    public Double getDTA() {
        return DTA;
    }

    public void setDTA(Double DTA) {
        this.DTA = DTA;
    }

    public Double getPpE() {
        return ppE;
    }

    public void setPpE(Double ppE) {
        this.ppE = ppE;
    }

    public Double getTP() {
        return TP;
    }

    public void setTP(Double TP) {
        this.TP = TP;
    }

    public Double getIP() {
        return IP;
    }

    public void setIP(Double IP) {
        this.IP = IP;
    }

    public Double getCTP() {
        return CTP;
    }

    public void setCTP(Double CTP) {
        this.CTP = CTP;
    }

    public Double getSTEP() {
        return STEP;
    }

    public void setSTEP(Double STEP) {
        this.STEP = STEP;
    }

    public Double getLTLP() {
        return LTLP;
    }

    public void setLTLP(Double LTLP) {
        this.LTLP = LTLP;
    }

    public Double getLTEP() {
        return LTEP;
    }

    public void setLTEP(Double LTEP) {
        this.LTEP = LTEP;
    }

    public Double getSC() {
        return SC;
    }

    public void setSC(Double SC) {
        this.SC = SC;
    }

    public Double getRE() {
        return RE;
    }

    public void setRE(Double RE) {
        this.RE = RE;
    }
}

class ie {
    Double revenue = 0.0;
    Double costOfGoodsSold = 0.0;
    Double operatingExpense = 0.0;
    Double advertsingExpense = 0.0;
    Double frieghtCost = 0.0;
    Double maintainanceCost = 0.0;
    Double employeeExpense = 0.0;
    Double otherOperatingExpense = 0.0;
    Double depreciationAmortization = 0.0;
    Double otherExpense = 0.0;
    Double interestExpense = 0.0;
    Double profitBeforeTax = 0.0;
    Double incomeTax = 0.0;
    Double profitForYear = 0.0;
    Double unrealized = 0.0;
    Double availableforsale = 0.0;
    Double totalIncome = 0.0;

    public ie() {
    }

    public ie(Double totalIncome, Double availableforsale, Double unrealized, Double revenue, Double costOfGoodsSold, Double operatingExpense, Double advertsingExpense, Double frieghtCost, Double maintainanceCost, Double employeeExpense, Double otherOperatingExpense, Double depreciationAmortization, Double otherExpense, Double interestExpense, Double profitBeforeTax, Double incomeTax, Double profitForYear) {
        this.totalIncome = totalIncome;
        this.availableforsale = availableforsale;
        this.unrealized = unrealized;
        this.revenue = revenue;
        this.costOfGoodsSold = costOfGoodsSold;
        this.operatingExpense = operatingExpense;
        this.advertsingExpense = advertsingExpense;
        this.frieghtCost = frieghtCost;
        this.maintainanceCost = maintainanceCost;
        this.employeeExpense = employeeExpense;
        this.otherOperatingExpense = otherOperatingExpense;
        this.depreciationAmortization = depreciationAmortization;
        this.otherExpense = otherExpense;
        this.interestExpense = interestExpense;
        this.profitBeforeTax = profitBeforeTax;
        this.incomeTax = incomeTax;
        this.profitForYear = profitForYear;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getAvailableforsale() {
        return availableforsale;
    }

    public void setAvailableforsale(Double availableforsale) {
        this.availableforsale = availableforsale;
    }

    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getCostOfGoodsSold() {
        return costOfGoodsSold;
    }

    public void setCostOfGoodsSold(Double costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
    }

    public Double getOperatingExpense() {
        return operatingExpense;
    }

    public void setOperatingExpense(Double operatingExpense) {
        this.operatingExpense = operatingExpense;
    }

    public Double getAdvertsingExpense() {
        return advertsingExpense;
    }

    public void setAdvertsingExpense(Double advertsingExpense) {
        this.advertsingExpense = advertsingExpense;
    }

    public Double getFrieghtCost() {
        return frieghtCost;
    }

    public void setFrieghtCost(Double frieghtCost) {
        this.frieghtCost = frieghtCost;
    }

    public Double getMaintainanceCost() {
        return maintainanceCost;
    }

    public void setMaintainanceCost(Double maintainanceCost) {
        this.maintainanceCost = maintainanceCost;
    }

    public Double getEmployeeExpense() {
        return employeeExpense;
    }

    public void setEmployeeExpense(Double employeeExpense) {
        this.employeeExpense = employeeExpense;
    }

    public Double getOtherOperatingExpense() {
        return otherOperatingExpense;
    }

    public void setOtherOperatingExpense(Double otherOperatingExpense) {
        this.otherOperatingExpense = otherOperatingExpense;
    }

    public Double getDepreciationAmortization() {
        return depreciationAmortization;
    }

    public void setDepreciationAmortization(Double depreciationAmortization) {
        this.depreciationAmortization = depreciationAmortization;
    }

    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public Double getInterestExpense() {
        return interestExpense;
    }

    public void setInterestExpense(Double interestExpense) {
        this.interestExpense = interestExpense;
    }

    public Double getProfitBeforeTax() {
        return profitBeforeTax;
    }

    public void setProfitBeforeTax(Double profitBeforeTax) {
        this.profitBeforeTax = profitBeforeTax;
    }

    public Double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(Double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public Double getProfitForYear() {
        return profitForYear;
    }

    public void setProfitForYear(Double profitForYear) {
        this.profitForYear = profitForYear;
    }


}

class ce{
    Double netIncome = 0.0;
    Double dividents = 0.0;
    Double issues = 0.0;
    Double repurchase = 0.0;

    Double SC = 0.0;
    Double RE = 0.0;
    Double total = 0.0;

    public ce() {
    }

    public ce(Double SC,Double RE,Double total, Double netIncome, Double dividents, Double issues, Double repurchase) {
        this.SC = SC;
        this.RE = RE;
        this.total = total;
        this.netIncome = netIncome;
        this.dividents = dividents;
        this.issues = issues;
        this.repurchase = repurchase;
    }

    public Double getSC() {
        return SC;
    }

    public void setSC(Double SC) {
        this.SC = SC;
    }

    public Double getRE() {
        return RE;
    }

    public void setRE(Double RE) {
        this.RE = RE;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(Double netIncome) {
        this.netIncome = netIncome;
    }

    public Double getDividents() {
        return dividents;
    }

    public void setDividents(Double dividents) {
        this.dividents = dividents;
    }

    public Double getIssues() {
        return issues;
    }

    public void setIssues(Double issues) {
        this.issues = issues;
    }

    public Double getRepurchase() {
        return repurchase;
    }

    public void setRepurchase(Double repurchase) {
        this.repurchase = repurchase;
    }
}

class repo {
    fp fp;
    ie ie;
    ce ce;
    cf cf;
    public repo() {
    }

    public repo(com.eyob.IFRSSystem.web.cf cf, com.eyob.IFRSSystem.web.ce ce, com.eyob.IFRSSystem.web.fp fp, com.eyob.IFRSSystem.web.ie ie) {
        this.ce = ce;
        this.fp = fp;
        this.ie = ie;
        this.cf = cf;
    }

    public com.eyob.IFRSSystem.web.cf getCf() {
        return cf;
    }

    public void setCf(com.eyob.IFRSSystem.web.cf cf) {
        this.cf = cf;
    }

    public com.eyob.IFRSSystem.web.ce getCe() {
        return ce;
    }

    public void setCe(com.eyob.IFRSSystem.web.ce ce) {
        this.ce = ce;
    }

    public com.eyob.IFRSSystem.web.fp getFp() {
        return fp;
    }

    public void setFp(com.eyob.IFRSSystem.web.fp fp) {
        this.fp = fp;
    }

    public com.eyob.IFRSSystem.web.ie getIe() {
        return ie;
    }

    public void setIe(com.eyob.IFRSSystem.web.ie ie) {
        this.ie = ie;
    }
}

class cf {
    Double Profit_for_the_year  = 0.0;
    Double Amortisation  = 0.0;
    Double Depreciation  = 0.0;
    Double impairment_loss  = 0.0;
    Double Cash_receipts_from_customers  = 0.0;
    Double Payments_to_employees  = 0.0;
    Double Payments_to_suppliers_of_services  = 0.0;
    Double Payments_to_suppliers_of_goods  = 0.0;
    Double Interest_paid  = 0.0;
    Double Income_taxes_paid  = 0.0;
    Double Decrease_increase_in_trade_and_other_receivables  = 0.0;
    Double Decrease_increase_in_inventories  = 0.0;
    Double Increase_decrease_in_trade_payables  = 0.0;
    Double Increase_decrease_in_current_and_long_term_employee_benefit_payable  = 0.0;
    Double Gain_on_sale_of_equipment  = 0.0;
    Double Net_cash_from_operating_activities  = 0.0;

    Double Proceeds_from_sale_of_equipment  = 0.0;
    Double Purchases_of_equipment  = 0.0;
    Double Net_cash_used_in_investing_activities  = 0.0;

    Double Payment_of_finance_lease_liabilities  = 0.0;
    Double Repayment_of_borrowings  = 0.0;
    Double Dividends_paid  = 0.0;
    Double Net_cash_used_in_financing_activities  = 0.0;


    Double Net_increase_decrease_in_cash_and_cash_equivalents  = 0.0;
    Double Cash_and_cash_equivalents_at_beginning_of_year  = 0.0;
    Double Cash_and_cash_equivalents_at_end_of_year  = 0.0;

    public cf() {
    }

    public cf(Double profit_for_the_year, Double amortisation, Double depreciation, Double impairment_loss, Double cash_receipts_from_customers, Double payments_to_employees, Double payments_to_suppliers_of_services, Double payments_to_suppliers_of_goods, Double interest_paid, Double income_taxes_paid, Double decrease_increase_in_trade_and_other_receivables, Double decrease_increase_in_inventories, Double increase_decrease_in_trade_payables, Double increase_decrease_in_current_and_long_term_employee_benefit_payable, Double gain_on_sale_of_equipment, Double net_cash_from_operating_activities, Double proceeds_from_sale_of_equipment, Double purchases_of_equipment, Double net_cash_used_in_investing_activities, Double payment_of_finance_lease_liabilities, Double repayment_of_borrowings, Double dividends_paid, Double net_cash_used_in_financing_activities, Double net_increase_decrease_in_cash_and_cash_equivalents, Double cash_and_cash_equivalents_at_beginning_of_year, Double cash_and_cash_equivalents_at_end_of_year) {
        this.Profit_for_the_year = profit_for_the_year;
        this.Amortisation = amortisation;
        this.Depreciation = depreciation;
        this.impairment_loss = impairment_loss;
        this.Cash_receipts_from_customers = cash_receipts_from_customers;
        this.Payments_to_employees = payments_to_employees;
        this.Payments_to_suppliers_of_services = payments_to_suppliers_of_services;
        this.Payments_to_suppliers_of_goods = payments_to_suppliers_of_goods;
        this.Interest_paid = interest_paid;
        this.Income_taxes_paid = income_taxes_paid;
        this.Decrease_increase_in_trade_and_other_receivables = decrease_increase_in_trade_and_other_receivables;
        this.Decrease_increase_in_inventories = decrease_increase_in_inventories;
        this.Increase_decrease_in_trade_payables = increase_decrease_in_trade_payables;
        this.Increase_decrease_in_current_and_long_term_employee_benefit_payable = increase_decrease_in_current_and_long_term_employee_benefit_payable;
        this.Gain_on_sale_of_equipment = gain_on_sale_of_equipment;
        this.Net_cash_from_operating_activities = net_cash_from_operating_activities;
        this.Proceeds_from_sale_of_equipment = proceeds_from_sale_of_equipment;
        this.Purchases_of_equipment = purchases_of_equipment;
        this.Net_cash_used_in_investing_activities = net_cash_used_in_investing_activities;
        this.Payment_of_finance_lease_liabilities = payment_of_finance_lease_liabilities;
        this.Repayment_of_borrowings = repayment_of_borrowings;
        this.Dividends_paid = dividends_paid;
        this.Net_cash_used_in_financing_activities = net_cash_used_in_financing_activities;
        this.Net_increase_decrease_in_cash_and_cash_equivalents = net_increase_decrease_in_cash_and_cash_equivalents;
        this.Cash_and_cash_equivalents_at_beginning_of_year = cash_and_cash_equivalents_at_beginning_of_year;
        this.Cash_and_cash_equivalents_at_end_of_year = cash_and_cash_equivalents_at_end_of_year;
    }

    public Double getProfit_for_the_year() {
        return Profit_for_the_year;
    }

    public void setProfit_for_the_year(Double profit_for_the_year) {
        Profit_for_the_year = profit_for_the_year;
    }

    public Double getAmortisation() {
        return Amortisation;
    }

    public void setAmortisation(Double amortisation) {
        Amortisation = amortisation;
    }

    public Double getDepreciation() {
        return Depreciation;
    }

    public void setDepreciation(Double depreciation) {
        Depreciation = depreciation;
    }

    public Double getImpairment_loss() {
        return impairment_loss;
    }

    public void setImpairment_loss(Double impairment_loss) {
        this.impairment_loss = impairment_loss;
    }

    public Double getCash_receipts_from_customers() {
        return Cash_receipts_from_customers;
    }

    public void setCash_receipts_from_customers(Double cash_receipts_from_customers) {
        Cash_receipts_from_customers = cash_receipts_from_customers;
    }

    public Double getPayments_to_employees() {
        return Payments_to_employees;
    }

    public void setPayments_to_employees(Double payments_to_employees) {
        Payments_to_employees = payments_to_employees;
    }

    public Double getPayments_to_suppliers_of_services() {
        return Payments_to_suppliers_of_services;
    }

    public void setPayments_to_suppliers_of_services(Double payments_to_suppliers_of_services) {
        Payments_to_suppliers_of_services = payments_to_suppliers_of_services;
    }

    public Double getPayments_to_suppliers_of_goods() {
        return Payments_to_suppliers_of_goods;
    }

    public void setPayments_to_suppliers_of_goods(Double payments_to_suppliers_of_goods) {
        Payments_to_suppliers_of_goods = payments_to_suppliers_of_goods;
    }

    public Double getInterest_paid() {
        return Interest_paid;
    }

    public void setInterest_paid(Double interest_paid) {
        Interest_paid = interest_paid;
    }

    public Double getIncome_taxes_paid() {
        return Income_taxes_paid;
    }

    public void setIncome_taxes_paid(Double income_taxes_paid) {
        Income_taxes_paid = income_taxes_paid;
    }

    public Double getDecrease_increase_in_trade_and_other_receivables() {
        return Decrease_increase_in_trade_and_other_receivables;
    }

    public void setDecrease_increase_in_trade_and_other_receivables(Double decrease_increase_in_trade_and_other_receivables) {
        Decrease_increase_in_trade_and_other_receivables = decrease_increase_in_trade_and_other_receivables;
    }

    public Double getDecrease_increase_in_inventories() {
        return Decrease_increase_in_inventories;
    }

    public void setDecrease_increase_in_inventories(Double decrease_increase_in_inventories) {
        Decrease_increase_in_inventories = decrease_increase_in_inventories;
    }

    public Double getIncrease_decrease_in_trade_payables() {
        return Increase_decrease_in_trade_payables;
    }

    public void setIncrease_decrease_in_trade_payables(Double increase_decrease_in_trade_payables) {
        Increase_decrease_in_trade_payables = increase_decrease_in_trade_payables;
    }

    public Double getIncrease_decrease_in_current_and_long_term_employee_benefit_payable() {
        return Increase_decrease_in_current_and_long_term_employee_benefit_payable;
    }

    public void setIncrease_decrease_in_current_and_long_term_employee_benefit_payable(Double increase_decrease_in_current_and_long_term_employee_benefit_payable) {
        Increase_decrease_in_current_and_long_term_employee_benefit_payable = increase_decrease_in_current_and_long_term_employee_benefit_payable;
    }

    public Double getGain_on_sale_of_equipment() {
        return Gain_on_sale_of_equipment;
    }

    public void setGain_on_sale_of_equipment(Double gain_on_sale_of_equipment) {
        Gain_on_sale_of_equipment = gain_on_sale_of_equipment;
    }

    public Double getNet_cash_from_operating_activities() {
        return Net_cash_from_operating_activities;
    }

    public void setNet_cash_from_operating_activities(Double net_cash_from_operating_activities) {
        Net_cash_from_operating_activities = net_cash_from_operating_activities;
    }

    public Double getProceeds_from_sale_of_equipment() {
        return Proceeds_from_sale_of_equipment;
    }

    public void setProceeds_from_sale_of_equipment(Double proceeds_from_sale_of_equipment) {
        Proceeds_from_sale_of_equipment = proceeds_from_sale_of_equipment;
    }

    public Double getPurchases_of_equipment() {
        return Purchases_of_equipment;
    }

    public void setPurchases_of_equipment(Double purchases_of_equipment) {
        Purchases_of_equipment = purchases_of_equipment;
    }

    public Double getNet_cash_used_in_investing_activities() {
        return Net_cash_used_in_investing_activities;
    }

    public void setNet_cash_used_in_investing_activities(Double net_cash_used_in_investing_activities) {
        Net_cash_used_in_investing_activities = net_cash_used_in_investing_activities;
    }

    public Double getPayment_of_finance_lease_liabilities() {
        return Payment_of_finance_lease_liabilities;
    }

    public void setPayment_of_finance_lease_liabilities(Double payment_of_finance_lease_liabilities) {
        Payment_of_finance_lease_liabilities = payment_of_finance_lease_liabilities;
    }

    public Double getRepayment_of_borrowings() {
        return Repayment_of_borrowings;
    }

    public void setRepayment_of_borrowings(Double repayment_of_borrowings) {
        Repayment_of_borrowings = repayment_of_borrowings;
    }

    public Double getDividends_paid() {
        return Dividends_paid;
    }

    public void setDividends_paid(Double dividends_paid) {
        Dividends_paid = dividends_paid;
    }

    public Double getNet_cash_used_in_financing_activities() {
        return Net_cash_used_in_financing_activities;
    }

    public void setNet_cash_used_in_financing_activities(Double net_cash_used_in_financing_activities) {
        Net_cash_used_in_financing_activities = net_cash_used_in_financing_activities;
    }

    public Double getNet_increase_decrease_in_cash_and_cash_equivalents() {
        return Net_increase_decrease_in_cash_and_cash_equivalents;
    }

    public void setNet_increase_decrease_in_cash_and_cash_equivalents(Double net_increase_decrease_in_cash_and_cash_equivalents) {
        Net_increase_decrease_in_cash_and_cash_equivalents = net_increase_decrease_in_cash_and_cash_equivalents;
    }

    public Double getCash_and_cash_equivalents_at_beginning_of_year() {
        return Cash_and_cash_equivalents_at_beginning_of_year;
    }

    public void setCash_and_cash_equivalents_at_beginning_of_year(Double cash_and_cash_equivalents_at_beginning_of_year) {
        Cash_and_cash_equivalents_at_beginning_of_year = cash_and_cash_equivalents_at_beginning_of_year;
    }

    public Double getCash_and_cash_equivalents_at_end_of_year() {
        return Cash_and_cash_equivalents_at_end_of_year;
    }

    public void setCash_and_cash_equivalents_at_end_of_year(Double cash_and_cash_equivalents_at_end_of_year) {
        Cash_and_cash_equivalents_at_end_of_year = cash_and_cash_equivalents_at_end_of_year;
    }
}

class reportdates {
    Date date_start;
    Date date_end;

    public reportdates() {
    }

    public reportdates(Date date_start, Date date_end) {
        this.date_start = date_start;
        this.date_end = date_end;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }
}