package com.bootdo.clouddozuul.filter;

import com.bootdo.clouddocommon.constants.CommonConstants;
import com.bootdo.clouddocommon.context.FilterContextHandler;
import com.bootdo.clouddocommon.dto.MenuDTO;
import com.bootdo.clouddocommon.dto.UserToken;
import com.bootdo.clouddocommon.utils.JSONUtils;
import com.bootdo.clouddocommon.utils.JwtUtils;
import com.bootdo.clouddocommon.utils.ResultVO;
import com.bootdo.clouddozuul.prc.admin.MenuService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;


@Slf4j
public class AccessFilter extends ZuulFilter {

    @Autowired
    MenuService menuService;


    private String ignorePath = "/api-admin/login";

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }


    @Override
    public Object run() {
       RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String requestUri = request.getRequestURI();
        if (isStartWith(requestUri)) {
            return null;
        }
        String accessToken = request.getHeader(CommonConstants.CONTEXT_TOKEN);
        if(null == accessToken || accessToken == ""){
            accessToken = request.getParameter(CommonConstants.TOKEN);
        }
        if (null == accessToken) {
            setFailedRequest(ResultVO.error401(), 200);
            return null;
        }
        try {
            UserToken userToken = JwtUtils.getInfoFromToken(accessToken);
        } catch (Exception e) {
            setFailedRequest(ResultVO.error401(), 200);
            return null;
        }
        FilterContextHandler.setToken(accessToken);
        if(!havePermission(request)){
            setFailedRequest(ResultVO.error403(), 200);
            return null;
        }
        Set<String> headers = (Set<String>) ctx.get("ignoredHeaders");
        headers.remove("authorization");
        return null;

    }

    private void setFailedRequest(Object body, int code) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        HttpServletResponse response = ctx.getResponse();
        PrintWriter out = null;
        try{
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.write(JSONUtils.beanToJson(body));
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
        ctx.setSendZuulResponse(false);
    }

    private boolean havePermission(HttpServletRequest request){
        String currentURL = request.getRequestURI();
        List<MenuDTO> menuDTOS = menuService.userMenus();
        for(MenuDTO menuDTO:menuDTOS){
            if(currentURL!=null&&null!=menuDTO.getUrl()&&currentURL.startsWith(menuDTO.getUrl())){
                return true;
            }
        }
        return false;
    }

    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : ignorePath.split(",")) {

            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }
}
