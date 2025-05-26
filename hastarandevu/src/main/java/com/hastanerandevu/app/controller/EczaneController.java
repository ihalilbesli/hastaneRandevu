package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.dto.Ezcane.EczaneDto;
import com.hastanerandevu.app.service.EczaneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hastarandevu/eczaneler")
public class EczaneController {

    private final EczaneService eczaneService;

    public EczaneController(EczaneService eczaneService) {
        this.eczaneService = eczaneService;
    }

    /**
     * Belirtilen şehirdeki (ve varsa ilçedeki) nöbetçi eczaneleri döner.
     *
     * @param city     Şehir adı (zorunlu)
     * @param district İlçe adı (opsiyonel)
     * @return EczaneDto listesi
     */
    @GetMapping
    public List<EczaneDto> getPharmacies(
            @RequestParam String city,
            @RequestParam(required = false) String district) {
        return eczaneService.getPharmacies(city, district);
    }
}
