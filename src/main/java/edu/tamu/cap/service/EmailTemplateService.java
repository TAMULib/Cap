package edu.tamu.cap.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.cap.model.EmailTemplate;

@Service
public class EmailTemplateService {

    @Autowired
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    public EmailTemplate buildEmail(String templateName, Map<String, String> parameters) {
        EmailTemplate emailTemplate = null;
        try {
            emailTemplate = getEmailTemplate(templateName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return templateParameters(emailTemplate, parameters);
    }

    private EmailTemplate getEmailTemplate(String templateName) throws IOException {
        return objectMapper.readValue(fileService.getFileFromResource("config/emails/" + templateName + ".json"), new TypeReference<EmailTemplate>() {});
    }

    private EmailTemplate templateParameters(EmailTemplate emailTemplate, Map<String, String> parameters) {
        emailTemplate.setSubject(templateEmailSection(emailTemplate.getSubject(), parameters));
        emailTemplate.setMessage(templateEmailSection(emailTemplate.getMessage(), parameters));
        return emailTemplate;
    }

    private String templateEmailSection(String emailSection, Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            emailSection = emailSection.replaceAll("\\{" + name + "\\}", parameters.get(name));
        }
        return emailSection;
    }

}
