package edu.tamu.cap.controller.ircontext;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.cap.service.TransactingIRService;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("ir-context/{type}/{irid}/transaction")
public class IRContextTransactionController {
    
    @RequestMapping(method = GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse makeQuery(HttpServletResponse response, TransactingIRService<?> irService) throws Exception {
        Cookie cookie = new Cookie("transaction", irService.startTransaction());
        cookie.setMaxAge(180);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);        
        return new ApiResponse(SUCCESS, "Transaction successfully created");
    }
    
}
