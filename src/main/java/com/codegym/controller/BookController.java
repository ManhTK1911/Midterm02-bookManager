package com.codegym.controller;

import com.codegym.model.Book;
import com.codegym.model.Category;
import com.codegym.service.BookService;
import com.codegym.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories")
    public Iterable<Category> categories() {
        return categoryService.findAll();
    }

    @GetMapping("/create-book")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/book/create");
        modelAndView.addObject("book", new Book());
        return modelAndView;
    }

    @PostMapping("/create-book")
    public ModelAndView saveBook(@ModelAttribute("book") Book book) {
        bookService.save(book);
        ModelAndView modelAndView = new ModelAndView("/book/create");
        modelAndView.addObject("book", new Book());
        modelAndView.addObject("message", "New book created successfully");
        return modelAndView;
    }

    @GetMapping("/books")
    public ModelAndView listBooks(@RequestParam("s") Optional<String> s, @PageableDefault(size = 5) Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("/book/list");
        Page<Book> books;
        if (s.isPresent()) {
            books = bookService.findAllByNameContaining(s.get(), pageable);
            modelAndView.addObject("issrch", "xxxx");
        } else {
            books = bookService.findAll(pageable);
        }
        modelAndView.addObject("books", books);
        return modelAndView;
    }

    @GetMapping("/searchByCategory")
    public ModelAndView getBookByCategory(@RequestParam("srch") String search, @PageableDefault(size = 5) Pageable pageable) {
        Category category = categoryService.findById(Long.parseLong(search));
        Page<Book> books = bookService.findAllByCategory(category, pageable);
        ModelAndView modelAndView = new ModelAndView("/book/list");
        modelAndView.addObject("books", books);
        modelAndView.addObject("srch", search);
        return modelAndView;
    }

    @GetMapping("/sortByPrice")
    public ModelAndView getListSortedByPrice(@RequestParam("sortDirection2") String sort, @PageableDefault(size = 5) Pageable pageable) {
        Page<Book> books;
        if (sort.equals("no")) {
            books = bookService.findAll(pageable);
        } else if (sort.equals("asc")) {
            books = bookService.findAllByOrderByPriceAsc(pageable);
        } else {
            books = bookService.findAllByOrderByPriceDesc(pageable);
        }
        ModelAndView modelAndView = new ModelAndView("/book/list");
        modelAndView.addObject("books", books);
        modelAndView.addObject("sortDirection2", sort);
        return modelAndView;
    }

    @GetMapping("/edit-book/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            ModelAndView modelAndView = new ModelAndView("/book/edit");
            modelAndView.addObject("book", book);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/edit-book")
    public ModelAndView updateBook(@ModelAttribute("book") Book book) {
        bookService.save(book);
        ModelAndView modelAndView = new ModelAndView("/book/edit");
        modelAndView.addObject("book", book);
        modelAndView.addObject("message", "Book update successful");
        return modelAndView;
    }

    @GetMapping("/delete-book/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            ModelAndView modelAndView = new ModelAndView("/book/delete");
            modelAndView.addObject("book", book);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/delete-book")
    public String deleteBook(@ModelAttribute("book") Book book) {
        bookService.remove(book.getId());
        return "redirect:books";
    }

    @GetMapping("/detail-book/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "/book/detail";
    }
}
