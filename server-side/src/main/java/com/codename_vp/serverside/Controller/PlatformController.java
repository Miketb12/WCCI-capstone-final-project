package com.codename_vp.serverside.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codename_vp.serverside.Entity.Platform;
import com.codename_vp.serverside.Service.PlatformService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PlatformController {
    @Autowired
    PlatformService platformService;


    @GetMapping("/platform")
    public List<Platform> getPlatforms() {
        return this.platformService.getPlatforms();
    }


}
