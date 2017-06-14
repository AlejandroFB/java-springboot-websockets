package com.alejandro.mancala;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller to load the main page.
 *
 * @author afernandez
 */
@Controller
public class LoadGameController {

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }
}