package waa.edu.onlineshopping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
//@RequestMapping(value = "/error")
public class ErrorController {

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied() {

        return "403";
    }
//    @RequestMapping(value = "/error", method = RequestMethod.GET)
//    public String accessError() {
//        return "403";
//    }




}

