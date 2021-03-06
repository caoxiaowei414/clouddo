package com.bootdo.clouddobase.controller;


import com.bootdo.clouddocommon.dto.LogDO;
import com.bootdo.clouddocommon.utils.PageUtils;
import com.bootdo.clouddocommon.utils.Query;
import com.bootdo.clouddocommon.utils.ResultVO;
import com.bootdo.clouddobase.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/log")
@RestController
public class LogController {
    @Autowired
    LogService logService;

    @GetMapping()
    ResultVO list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        return ResultVO.page(new PageUtils(logService.queryList(query), logService.count(query)));
    }

    @PostMapping("/save")
    ResultVO save(@RequestBody LogDO logDO) {
        if (logService.save(logDO) > 0) {
            return ResultVO.ok();
        }
        return ResultVO.error();
    }

    @DeleteMapping()
    ResultVO remove(Long id) {
        if (logService.remove(id) > 0) {
            return ResultVO.ok();
        }
        return ResultVO.error();
    }

    @PostMapping("/batchRemove")
    ResultVO batchRemove(@RequestParam("ids[]") Long[] ids) {
        int r = logService.batchRemove(ids);
        if (r > 0) {
            return ResultVO.ok();
        }
        return ResultVO.error();
    }
}
