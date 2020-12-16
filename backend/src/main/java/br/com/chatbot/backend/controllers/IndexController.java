package br.com.chatbot.backend.controllers;

import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class IndexController implements View {
    @Override
    public String getContentType() {
        return "index";
    }

    @Override
    public void render(Map<String, ?> map, @NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse) {
    }

    @GetMapping("/web")
    public String index(Model model) {
        return getContentType();
    }

    @GetMapping("/web/**")
    public String otherPages(Model model) {
        return getContentType();
    }
}
