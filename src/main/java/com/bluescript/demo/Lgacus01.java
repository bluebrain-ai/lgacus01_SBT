package com.bluescript.demo;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import reactor.core.publisher.Mono;
import io.swagger.annotations.ApiResponses;
import com.bluescript.demo.model.WsHeader;
import com.bluescript.demo.model.EmVariable;
import com.bluescript.demo.model.ErrorMsg;
import com.bluescript.demo.model.Dfhcommarea;
import com.bluescript.demo.model.CaCustomerRequest;
import com.bluescript.demo.model.CaCustsecrRequest;
import com.bluescript.demo.model.CaPolicyRequest;
import com.bluescript.demo.model.CaPolicyCommon;
import com.bluescript.demo.model.CaEndowment;
import com.bluescript.demo.model.CaHouse;
import com.bluescript.demo.model.CaMotor;
import com.bluescript.demo.model.CaCommercial;
//import com.bluescript.demo.mapper.ConvStrToObj;
import com.bluescript.demo.model.CaClaim;

@Getter
@Setter
@RequiredArgsConstructor
@Log4j2
@Component

@RestController
@RequestMapping("/")
@ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 400, message = "This is a bad request, please follow the API documentation for the proper request format"),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Due to security constraints, your access request cannot be authorized"),
        @io.swagger.annotations.ApiResponse(code = 500, message = "The server/Application is down. Please contact support team.") })
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Lgacus01 {

    @Autowired
    private WsHeader wsHeader;
    @Autowired
    private EmVariable emVariable;
    @Autowired
    private Dfhcommarea dfhcommarea;
    @Autowired
    private CaCustomerRequest caCustomerRequest;
    @Autowired
    private CaCustsecrRequest caCustsecrRequest;
    @Autowired
    private CaPolicyRequest caPolicyRequest;
    @Autowired
    private CaPolicyCommon caPolicyCommon;
    @Autowired
    private CaEndowment caEndowment;
    @Autowired
    private CaHouse caHouse;
    @Autowired
    private CaMotor caMotor;
    @Autowired
    private CaCommercial caCommercial;
    @Autowired
    private CaClaim caClaim;
    private String wsTime;
    private String wsDate;
    private String emDate;
    private String emTime;
    private String caData;
    private int wsCaHeaderLen = 0;
    private int wsRequiredCaLen = 0;
    private int wsCustomerLen = 0;
    private int eibcalen;
    private String wsAbstime;
    @Autowired
    // private ConvStrToObj convStrToObj;

    @Value("${api.LGACDB01.uri}")
    private String LGCDAB01_HOST;
    @Value("${api.LGACDB01.uri}")
    private String LGACDB01_URI;
    @Value("${api.LGSTSQ.uri}")
    private String LGSTSQ_URI;
    @Value("${api.LGSTSQ.host}")
    private String LGSTSQ_HOST;
    @Autowired
    private ErrorMsg errorMsg;
    private String caErrorMsg;

    @PostMapping("/lgacus01")
    public ResponseEntity<Dfhcommarea> main(@RequestBody Dfhcommarea payload) {
        // if(eibcalen==0) //Move to Bean Validateion , Pattern based automation
        // {
        // wsHeader.setEmVariable(" NO COMMAREA RECEIVED");
        // writeErrorMessage();
        // log.error("Error code : LGCA");
        // throw new Exception("LGCA");

        // }
        BeanUtils.copyProperties(payload, dfhcommarea);
        log.info(dfhcommarea.getCaCustomerRequest().toString());

        // caCustomerRequest = convStrToObj.caRequestSpecificTocaCustomerRequest_1(payload,
        // caCustomerRequest);
        dfhcommarea.setCaReturnCode(00);
        caCustomerRequest.setCaNumPolicies(00);
        /*
         * --> this will be replaced with constraint valudation wsRequiredCaLen = wsCaHeaderLen + wsRequiredCaLen;
         * wsRequiredCaLen = wsCustomerLen + wsRequiredCaLen; if (eibcalen < wsRequiredCaLen) {
         * dfhcommarea.setCaReturnCode(98);
         * 
         * }
         */
        insertCustomer();

        return new ResponseEntity<>(dfhcommarea, HttpStatus.OK);

    }

    public void insertCustomer() {
        log.debug("MethodinsertCustomerstarted..");
        try {
            WebClient webclientBuilder = WebClient.create(LGCDAB01_HOST);
            Mono<Dfhcommarea> lgacdb01Resp = webclientBuilder.post().uri(LGACDB01_URI)
                    .body(Mono.just(dfhcommarea), Dfhcommarea.class).retrieve().bodyToMono(Dfhcommarea.class)
                    .timeout(Duration.ofMillis(10_000));
            dfhcommarea = lgacdb01Resp.block();
        } catch (Exception e) {
            log.error(e);
        }

        log.debug("Method insertCustomer completed..");
    }

    public void writeErrorMessage() {
        log.debug("MethodwriteErrorMessagestarted..");
        wsAbstime = LocalTime.now().toString();
        wsAbstime = LocalTime.now().toString();
        wsDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMDDYYYY"));
        wsTime = LocalTime.now().toString();
        emDate = wsDate.substring(0, 8);
        emTime = wsTime.substring(0, 6);
        WebClient webclientBuilder = WebClient.create(LGSTSQ_HOST);
        try {
            Mono<ErrorMsg> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                    .body(Mono.just(errorMsg), ErrorMsg.class).retrieve().bodyToMono(ErrorMsg.class)
                    .timeout(Duration.ofMillis(10_000));
            errorMsg = lgstsqResp.block();
        } catch (Exception e) {
            log.error(e);
        }
        if (eibcalen > 0) {
            if (eibcalen < 91) {
                caData = (dfhcommarea.getCaRequestId().substring(0, 9));
                try {
                    Mono<String> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                            .body(Mono.just(caErrorMsg), String.class).retrieve().bodyToMono(String.class)
                            .timeout(Duration.ofMillis(10_000));
                    caErrorMsg = lgstsqResp.block();
                } catch (Exception e) {
                    log.error(e);
                }

            } else {
                caData = (dfhcommarea.getCaRequestId() + dfhcommarea.getCaReturnCode() + dfhcommarea.getCaCustomerNum()
                        + dfhcommarea.getCaRequestSpecific().substring(0, 72));
                try {
                    Mono<String> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                            .body(Mono.just(caErrorMsg), String.class).retrieve().bodyToMono(String.class)
                            .timeout(Duration.ofMillis(10_000));
                    caErrorMsg = lgstsqResp.block();
                } catch (Exception e) {
                    log.error(e);
                }

            }

        }

        log.debug("Method writeErrorMessage completed..");
    }

    /* End of program */
}