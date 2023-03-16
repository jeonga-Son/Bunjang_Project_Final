package com.example.demo.src.point;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.point.model.GetPointListRes;
import com.example.demo.src.point.model.GetPointRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointController {
    @Autowired
    private final PointProvider pointProvider;

    @Autowired
    private final JwtService jwtService;

    public PointController(PointProvider pointProvider, JwtService jwtService) {
        this.pointProvider = pointProvider;
        this.jwtService = jwtService;
    }

    /**
     * 포인트 조회 API
     * [GET] /points?userIdx={userIdx}
     * @return BaseResponse<GetPointRes>
     */
    @GetMapping("")
    public BaseResponse<List<GetPointRes>> getMyPoint(@RequestParam("userIdx") int userIdx) {
        try {
            List<GetPointRes> getPointRes = pointProvider.getMyPoint(userIdx);
            return new BaseResponse<>(getPointRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 포인트 사용 내역 조회 API
     * [GET] /points?pointList/userIdx={userIdx}
     * @return BaseResponse<GetPointRes>
     */
    @GetMapping("/details")
    public BaseResponse<List<GetPointListRes>> getMyPointList(@RequestParam("userIdx") int userIdx) {
        try {
            List<GetPointListRes> getPointListRes = pointProvider.getMyPointList(userIdx);
            return new BaseResponse<>(getPointListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
