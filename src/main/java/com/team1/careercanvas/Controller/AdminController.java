package com.team1.careercanvas.Controller;

import com.team1.careercanvas.mapper.AdminMapper;
import com.team1.careercanvas.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminController {
    private final AdminMapper mapper;

    public AdminController(AdminMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/admin/member") // 개인회원관리
    public ModelAndView member(HttpSession session,
            @RequestParam(required = false, defaultValue = "1") int postSort,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String searchWord,
            @RequestParam(required = false, defaultValue = "1") int page) {
        ModelAndView mav = new ModelAndView();
        PagingVO pvo = new PagingVO();
        pvo.setSearchKey(searchKey);
        pvo.setSearchWord(searchWord);
        pvo.setPostSort(postSort);
        pvo.setPage(page);
        pvo.setTotalRecord(mapper.getUserCount());

        String name = mapper.getAdminName((String) session.getAttribute("LogId"));
        mav.addObject("name", name);

        List<UserVO> list = mapper.getUserProfile(pvo);
        mav.addObject("uVO", list);
        mav.addObject("pVO", pvo);
        mav.setViewName("/admin/admin_member");

        return mav;
    }

    @GetMapping("/admin/company") // 기업회원관리
    public ModelAndView company(HttpSession session) {
        ModelAndView mav = new ModelAndView();

        String name = mapper.getAdminName((String) session.getAttribute("LogId"));
        mav.addObject("name", name);

        mav.setViewName("/admin/admin_company");

        return mav;
    }

    @GetMapping("/admin/board") // 게시판 - 실시간 모니터링
    public ModelAndView board(HttpSession session,
            @RequestParam(required = false, defaultValue = "1") int page) {
        ModelAndView mav = new ModelAndView();

        PagingVO pvo = new PagingVO();
        pvo.setPage(page);
        pvo.setTotalRecord(mapper.getBoardCount());

        String name = mapper.getAdminName((String) session.getAttribute("LogId"));
        mav.addObject("name", name);

        List<BoardVO> list = mapper.getBoardList();
        mav.addObject("bVO", list);
        mav.addObject("pVO", pvo);
        mav.setViewName("/admin/admin_board");

        return mav;
    }

    @GetMapping("/admin/report") // 신고 게시글 관리
    public ModelAndView report(HttpSession session,
            @RequestParam(required = false, defaultValue = "1") int page) {
        ModelAndView mav = new ModelAndView();

        PagingVO pvo = new PagingVO();
        pvo.setPage(page);
        pvo.setTotalRecord(mapper.getReportCount());

        String name = mapper.getAdminName((String) session.getAttribute("LogId"));
        mav.addObject("name", name);

        List<ReportVO> list = mapper.getReportList();
        mav.addObject("rVO", list);
        mav.addObject("pVO", pvo);
        mav.setViewName("admin/report_board");

        return mav;
    }

    @GetMapping("/admin/delete") // 삭제 신청 과제 리스트
    public ModelAndView assignment(HttpSession session,
            @RequestParam(required = false, defaultValue = "1") int page) {
        ModelAndView mav = new ModelAndView();

        PagingVO pvo = new PagingVO();
        pvo.setPage(page);
        pvo.setTotalRecord(mapper.getDeleteCount());

        String name = mapper.getAdminName((String) session.getAttribute("LogId"));
        mav.addObject("name", name);

        List<SubjectVO> list = mapper.getDeleteList();
        mav.addObject("sVO", list);
        mav.addObject("pVO", pvo);
        mav.setViewName("admin/delete_assignment");

        return mav;
    }

    @GetMapping("/subject/delete") // 삭제 신청 과제 삭제
    public String deleteAssignment(HttpSession session, int subjectid) {
        mapper.dismissSubject(subjectid);
        int result = mapper.deleteAssignment(subjectid);
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/delete";
        } else { // 삭제 실패
            session.setAttribute("msg", "삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/subject/dismiss") // 삭제 신청 과제 거절
    public String dismissAssignment(HttpSession session, int subjectid) {
        int result = mapper.dismissSubject(subjectid);
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/delete";
        } else { // 삭제 실패
            session.setAttribute("msg", "삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/board/delete") // 모니터링 페이지 게시글 삭제
    public String deleteBoard(HttpSession session, int postid) {
        int result = mapper.deleteBoard(postid);
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/board";
        } else { // 삭제 실패
            session.setAttribute("msg", "삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/report/dismiss") // 기각해서 리포트 테이블만 삭제
    public String dismissReport(HttpSession session, int targetid) {
        int result = mapper.deleteReport(targetid);
        // 보드에 삭제
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/report";
        } else { // 삭제 실패
            session.setAttribute("msg", "삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/report/delete") // 삭제 버튼 클릭 이벤트
    public String deleteReport(HttpSession session, int targetid, String reporttype) {
        System.out.println(reporttype);
        int Rresult = mapper.deleteReport(targetid);
        if (Rresult > 0) {
            if (reporttype.equals("board")) {
                int Bresult = mapper.deleteBoard(targetid);
                if (Bresult > 0) {
                    System.out.println("게시글 삭제 완료");
                    return "redirect:/admin/report";
                } else {
                    session.setAttribute("msg", "글 삭제 실패했습니다.");
                    return "alert_page";
                }
            } else if (reporttype.equals("comment")) {
                int Cresult = mapper.deleteComment(targetid);
                if (Cresult > 0) {
                    System.out.println("댓글 삭제 완료");
                    return "redirect:/admin/report";
                } else {
                    session.setAttribute("msg", "삭제 실패했습니다.");
                    return "alert_page";
                }
            } else if (reporttype.equals("wanted")) {
                int Wresult = mapper.deleteWanted(targetid);
                if (Wresult > 0) {
                    System.out.println("파티 모집글 삭제 완료");
                    return "redirect:/admin/report";
                } else {
                    session.setAttribute("msg", "삭제 실패했습니다.");
                    return "alert_page";
                }
            } else if (reporttype.equals("wantedcomment")) {
                int WCresult = mapper.deleteWantedComment(targetid);
                if (WCresult > 0) {
                    System.out.println("파티 모집 댓글 삭제 완료");
                    return "redirect:/admin/report";
                } else {
                    session.setAttribute("msg", "삭제 실패했습니다.");
                    return "alert_page";
                }
            }
            // ---------------------------- 추가되면 여기다 작업
        } else {
            session.setAttribute("msg", "삭제 실패했습니다.");
            return "alert_page";
        }
        return "redirect:/admin/report";
    }

    @GetMapping("/report/delete/user") // 탈퇴 - 리포트 테이블에서 userid 뽑고, 리포트 앤드 보드 삭제 -> user테이블에서 삭제
    public String reportDeleteUser(HttpSession session, int target_id) {
        String userid = mapper.reporterUser(target_id);
        int Rresult = mapper.deleteReport(target_id);
        int Bresult = mapper.deleteBoard(target_id);
        int Uresult = mapper.deleteUser(userid);

        if (Uresult > 0) {
            if (Rresult > 0 && Bresult > 0) { // 삭제 성공
                return "redirect:/admin/report";
            } else { // 삭제 실패
                session.setAttribute("msg", "삭제 실패했습니다.");
                return "alert_page";
            }
        } else {
            session.setAttribute("msg", "유저 삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/delete/user") // 강제 탈퇴 - 일반회원관리
    public String deleteUser(HttpSession session, String userid) {
        int result = mapper.deleteUser(userid);
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/member";
        } else { // 삭제 실패
            session.setAttribute("msg", "유저 삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/board/delete/user") // 강제 탈퇴 - 실시간 모니터링 강제탈퇴
    public String boardDeleteUser(HttpSession session, int postid) {
        String userid = mapper.getWriterUser(postid);
        int Uresult = mapper.deleteUser(userid);
        mapper.deleteBoard(postid);

        if (Uresult > 0) {
            return "redirect:/admin/board";
        } else {
            session.setAttribute("msg", "유저 삭제 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/change/user") // 닉네임 강제 변경
    public String changeUser(HttpSession session, String username) {
        String changed = "불건전한닉네임" + username;
        int result = mapper.changeUsername(username, changed);
        if (result > 0) { // 삭제 성공
            return "redirect:/admin/member";
        } else { // 삭제 실패
            session.setAttribute("msg", "닉네임 변경 실패했습니다.");
            return "alert_page";
        }
    }

    @GetMapping("/admin/user/stats")
    public ModelAndView userStats() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("member",mapper.getUserCount());
        mav.addObject("company",mapper.getCompanyCount());
        mav.addObject("newMember",mapper.getNewMember());
        mav.addObject("accessor",mapper.getAccessor());
        mav.setViewName("admin/admin_user_stats");
        return mav;
    }

    @GetMapping("/admin/board/stats")
    public ModelAndView boardStats() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("bVO", mapper.getBoardCount());
        mav.addObject("rVO", mapper.getReportCount());
        mav.addObject("today", mapper.getBoardToday());
        mav.addObject("month", mapper.getBoardMonth());
        mav.addObject("category", mapper.getBoardCategory());
        System.out.println(mapper.getBoardMonth());
        System.out.println(mapper.getBoardCategory());
        mav.setViewName("admin/admin_board_stats");
        return mav;
    }

    // 권혁준 작업
    @GetMapping("/admin/banner")
    public ModelAndView banner(PagingVO pVO) {
        ModelAndView mav = new ModelAndView();
        List<BannerVO> bVO = mapper.getBannerList();
        pVO.setOnePageRecord(5);
        pVO.setTotalRecord(mapper.getBannerAmount(pVO));
        pVO.setPage(pVO.getPage());

        System.out.println(pVO);
        mav.addObject("bannerVO", bVO);
        mav.setViewName("/admin/admin_banner");
        return mav;
    }

    //조석훈 작업
    @GetMapping("/admin/banner/add")
    public String banner_add(){
        return "admin/admin_banner_add";
    }

    @PostMapping("/admin/banner/addOk")
    public String banner_addOk(String startdate,
                               String deadline,
                               String owner,
                               MultipartFile bannerimg){
        //db에 배너정보 넣어주고 그냥 배너페이지로 redirect
        BannerVO bvo = new BannerVO();
        bvo.setStartdate(startdate);
        bvo.setDeadline(deadline);
        bvo.setOwner(owner);
        int result = mapper.InsertBanner(bvo);
        //bvo에 id는 들어왔음
        //파일 업로드처리
        if (!bannerimg.isEmpty()) {
            // 파일저장시작
            String extension = bannerimg.getOriginalFilename().substring(bannerimg.getOriginalFilename().lastIndexOf("."));
            String newFileName = bvo.getBannerid() + "_" +extension;
            String projectDir = new File("").getAbsolutePath();
            File directory = new File(projectDir + "/upload/bannerimg");
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리 생성
            }

            Path path = Paths.get(directory.getAbsolutePath(), newFileName); // 절대 경로를 사용

            try {
                bannerimg.transferTo(new File(path.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("파일저장실패");
                return "404pages";
            }
            // 파일저장 끝

            // db에 경로넣기
            String imgsrc = "/bannerimg/" + newFileName;

            mapper.InsertBannerimg(imgsrc, bvo.getBannerid());
        }
        //일단 insert는 했는데 bannerid를 얻어내서 이미지를 업로드하고 db에 또 반영해야함

        return "redirect:/admin/banner";
    }

}