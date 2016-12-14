package spittr.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import spittr.Spitter;
import spittr.data.SpitterRepository;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/spitter")
public class SpitterController {

  private SpitterRepository spitterRepository;

  @Autowired
  public SpitterController(SpitterRepository spitterRepository) {
    this.spitterRepository = spitterRepository;
  }
  
  @RequestMapping(value="/register", method=GET)
  public String showRegistrationForm(Model model) {
    model.addAttribute(new Spitter());
    return "registerForm";
  }
  
//  @RequestMapping(value="/register", method=POST)
//  public String processRegistration(
//      @RequestPart(value="profilePictures", required=false) Part fileBytes,
//      RedirectAttributes redirectAttributes,
//      @Valid Spitter spitter,
//      Errors errors) throws IOException {
//    if (errors.hasErrors()) {
//      return "registerForm";
//    }
//    
//    spitterRepository.save(spitter);
//    redirectAttributes.addAttribute("username", spitter.getUsername());
//    redirectAttributes.addFlashAttribute(spitter);
//    return "redirect:/spitter/" + spitter.getUsername();
//  }
  
  @RequestMapping(value="/register", method=POST)
  public String processRegistration(
      @RequestPart("profilePicture") byte[] profilepicture,
      @Valid SpitterForm spitterForm,
      Errors errors) throws IllegalStateException, IOException {
    
    if (errors.hasErrors()) {
      return "registerForm";
    }
    Spitter spitter = spitterForm.toSpitter();
    spitterRepository.save(spitter);
    MultipartFile profilePicture = spitterForm.getProfilePicture();
    profilePicture.transferTo(new File("/tmp/spittr/" + spitter.getUsername() + ".jpg"));  //将上传的文件写入到文件系统中
    return "redirect:/spitter/" + spitter.getUsername();   /*重定向*/
  }
  /*@RequestMapping(value = "/register",method = POST)
  public String processRegistration(Spitter spitter,Model model){
    spitterRepository.save(spitter);
    model.addAttribute("username",spitter.getUsername());
    return "redirect:/spitter/{username}";
  }
  username作为占位符填充到了URL模板中，而不是直接连接到重定向String中，所
  以username中所有的不安全字符都会进行转义。这样会更加安全，这里允许用户输入任何想
  要的内容作为username，并会将其附加到路径上。
*/
  @RequestMapping(value="/{username}", method=GET)
  public String showSpitterProfile(
          @PathVariable String username, Model model) {
    if (!model.containsAttribute("spitter")) {
      model.addAttribute(
          spitterRepository.findByUsername(username));
    }
    return "profile";
  }
  
}
