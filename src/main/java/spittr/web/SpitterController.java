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

 /* @RequestMapping(value = "/register",method = POST)
  public String processRegistration(Spitter spitter,RedirectAttributes model){
    spitterRepository.save(spitter);
    model.addAttribute("username",spitter.getUsername());
    model.addAttribute("spitter",spitter);
    return "redirect:/spitter/{username}";
  }
  我们调用了addFlashAttribute()方法，并将spitter作为key，Spitter对象作
  为值。另外，我们还可以不设置key参数，让key根据值的类型自行推断得出：
  因为我们传递了一个Spitter对象给addFlashAttribute()方法，所以推断得到的key将
  会是spitter。
  在重定向执行之前，所有的flash属性都会复制到会话中。在重定向后，存在会话中的flash属性
  会被取出，并从会话转移到模型之中。处理重定向的方法就能从模型中访问Spitter对象了，就
  像获取其他的模型对象一样。*/
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
