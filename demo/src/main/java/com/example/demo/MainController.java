package com.example.demo;


import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
public class MainController {

    // @Autowired
    // private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JoinRequestRepository joinRequestRepository;
    
    @Autowired
    private UserService userService;

    @SuppressWarnings("unused")
    private final MyService myService;
    
    public MainController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/")
    public String verifyLogin(@ModelAttribute PostDto postDto, HttpSession session, Model model, 
    HttpServletResponse response, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            // String name = user.getName();
            // Post info = postDto.toEntity();
            // info.setWriter(name);
            // postRepository.save(info);
            // model.addAttribute("info", info);
            return "redirect:/mainMenu";
        } else {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

    }
    
    @GetMapping("/login")
    public String showLoginForm(HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("userId") String userId, @RequestParam("pw") String password, 
    Model model, HttpSession session, HttpServletResponse response) {
        
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        
        Optional<User_info> user = userRepository.findByUserId(userId);
        session.removeAttribute("user");
        if (user.isPresent() && user.get().getPw().equals(password)) {
            session.setAttribute("userId", userId);
            return "redirect:/mainMenu";
        } else {
            model.addAttribute("message", "일치하는 정보가 없습니다.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        session.removeAttribute("userId");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        model.addAttribute("user_info", new User_info()); 
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute dtoRegister dto, Model model, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        if (userRepository.existsByName(dto.getName())) {
            model.addAttribute("message", "이미 존재하는 닉네임입니다.");
            model.addAttribute("user_info", dto);
            return "register"; 
        }

        if (userRepository.existsByUserId(dto.getUserId())) {
            model.addAttribute("message", "이미 존재하는 아이디입니다.");
            model.addAttribute("user_info", dto);
            return "register";
        }

        userRepository.save(dto.toEntity());
        model.addAttribute("message", "가입이 완료되었습니다!");
        return "login";
    }

    @PostMapping("/applyCrew/{adminId}/{postId}")
    public String applyToCrew(@PathVariable Long adminId, @PathVariable Long postId, Model model, 
                               HttpSession session, RedirectAttributes redirectAttributes) {

        String userId = (String) session.getAttribute("userId");
        User_info currentUser = userRepository.findByUserId(userId).orElse(null);

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "login";
        }

        if (currentUser.getCrew() != null) {
            redirectAttributes.addFlashAttribute("message", "이미 크루에 속해있습니다.");
            return "redirect:/post/" + postId;
        }

        User_info admin = userRepository.findById(adminId).orElse(null);
        Crew adminCrew = admin.getCrew();

        if (adminCrew == null) {
            redirectAttributes.addFlashAttribute("message", "크루를 찾을 수 없습니다.");
            return "redirect:/post/" + postId;
        }

        if (adminCrew.isFull()) {
            redirectAttributes.addFlashAttribute("message", "크루의 정원이 초과되었습니다.");
            return "redirect:/post/" + postId;
        }

        for (JoinRequest request : adminCrew.getJoinRequests()) {
            if (request.getUser().equals(currentUser)) {
                redirectAttributes.addFlashAttribute("message", "이미 가입 신청을 했습니다.");
                return "redirect:/post/" + postId;
            }
        }

        JoinRequest joinRequest = new JoinRequest(currentUser, adminCrew, LocalDateTime.now());
        adminCrew.addJoinRequest(joinRequest);
        joinRequestRepository.save(joinRequest);
        crewRepository.save(adminCrew);

        redirectAttributes.addFlashAttribute("message", "가입 신청이 완료되었습니다.");
        return "redirect:/post/" + postId;
    }

    @GetMapping("/approvalJoinRequest")
    public String getJoinRequests(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        User_info currentUser = userRepository.findByUserId(userId).orElse(null);

        if (currentUser == null || currentUser.getCrew() == null || !currentUser.getCrew().getAdmin().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "접근 권한이 없습니다.");
            return "redirect:/myCrew";
        }

        Crew crew = currentUser.getCrew();
        List<JoinRequest> joinRequests = joinRequestRepository.findByCrew(crew);

        model.addAttribute("joinRequests", joinRequests);
        return "approvalJoinRequest"; // HTML 파일 이름
    }

    @PostMapping("/approveJoinRequest/{id}")
    public String approveJoinRequest(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        User_info currentUser = userRepository.findByUserId(userId).orElse(null);

        if (currentUser == null || currentUser.getCrew() == null || !currentUser.getCrew().getAdmin().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "접근 권한이 없습니다.");
            return "redirect:/myCrew";
        }

        JoinRequest joinRequest = joinRequestRepository.findById(id).orElse(null);

        if (joinRequest != null && joinRequest.getCrew().equals(currentUser.getCrew())) {
            Crew crew = joinRequest.getCrew();
            User_info user = joinRequest.getUser();

            if (crew.isFull()) {
                redirectAttributes.addFlashAttribute("message", "크루의 정원이 초과되었습니다.");
                return "redirect:/approvalJoinRequest";
            }

            crew.addMember(user);
            crew.removeJoinRequest(joinRequest);
            user.setCrew(crew);
            userRepository.save(user);
            crewRepository.save(crew);
            joinRequestRepository.delete(joinRequest);

            
        }

        redirectAttributes.addFlashAttribute("message", "가입 신청이 승인되었습니다.");
        return "redirect:/approvalJoinRequest";
    }

    @PostMapping("/rejectJoinRequest/{id}")
    public String rejectJoinRequest(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        User_info currentUser = userRepository.findByUserId(userId).orElse(null);

        if (currentUser == null || currentUser.getCrew() == null || !currentUser.getCrew().getAdmin().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "접근 권한이 없습니다.");
            return "redirect:/myCrew";
        }

        JoinRequest joinRequest = joinRequestRepository.findById(id).orElse(null);

        if (joinRequest != null && joinRequest.getCrew().equals(currentUser.getCrew())) {
            Crew crew = joinRequest.getCrew();
            crew.removeJoinRequest(joinRequest);
            joinRequestRepository.delete(joinRequest);
        }
        
        redirectAttributes.addFlashAttribute("message", "가입 신청이 거절되었습니다.");
        return "redirect:/approvalJoinRequest";
    }


    @GetMapping("/joinRequestsCount")
    public ResponseEntity<Long> getJoinRequestsCount(@RequestParam Long crewId, HttpSession session, HttpServletResponse response) {
        long count = joinRequestRepository.countByCrewId(crewId);
        System.out.println("Crew ID: " + crewId); // 디버깅용 출력
        System.out.println("Join Request Count: " + count); // 디버깅용 출력
        
        
        return ResponseEntity.ok(count);
    }


    @GetMapping("/board")
    public String board(@RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "type", defaultValue = "free") String type,
                        @PageableDefault(size = 5) Pageable pageable,
                        Model model, HttpServletResponse response,
                        HttpSession session, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Post> postsPage;
        if (search == null || search.isEmpty()) {
            postsPage = postRepository.findByType(type, sortedPageable); // 카테고리 타입으로 조회
        } else {
            postsPage = postRepository.findByTypeAndTitleContainingOrTypeAndContentContaining(
                type, search, type, search, sortedPageable); // 카테고리 타입과 검색어로 조회
        }

        int totalPages = postsPage.getTotalPages();
        int currentPage = postsPage.getNumber() + 1;
        int pageDisplayRange = 5; // 보여줄 페이지 번호의 개수
        int startPage = Math.max(1, currentPage - pageDisplayRange / 2);
        int endPage = Math.min(totalPages, startPage + pageDisplayRange - 1);

        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("search", search);
        model.addAttribute("type", type); // 현재 선택된 타입 전달

        return "board";
    }


    @GetMapping("/write")
    public String createPost(@RequestParam(value = "type", required = false, defaultValue = "free") String type, 
    HttpSession session, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");


        if (session != null && session.getAttribute("userId") != null) {

            System.out.println("type: " + type);
            if (type.equals("crew")) {
                String userId = (String) session.getAttribute("userId");
                User_info userData = userRepository.findByUserId(userId).orElse(null);

                System.out.println("userData: " + userData);
                
                if (userData.getCrew() == null) {
                    redirectAttributes.addFlashAttribute("message", "크루 관리자만 작성할 수 있습니다.");
                    return "redirect:/board?type=" + type;
                } else {

                    if (userData.getCrew().getAdmin().equals(userData)) {
                        model.addAttribute("type", type);
                        return "write";

                    } else {
                        redirectAttributes.addFlashAttribute("message", "크루 관리자만 작성할 수 있습니다.");
                        return "redirect:/board?type=" + type;
                    }
                    
                }
            }
            

            model.addAttribute("type", type);
            return "write";
        } else {
            return "redirect:/login";
        }
    }


    @PostMapping("/boardpost")
    public String post(@ModelAttribute PostDto postDto, HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info user = userRepository.findByUserId(userId).orElse(null);
        if (user != null) {
            String writer = user.getName();
            int profileImg = user.getProfileImage();
            Post post = postDto.toEntity();
            post.setUser(user);
            post.setWriter(writer);
            post.setProfileImg(profileImg);
            postRepository.save(post);
            return "redirect:/board?type=" + postDto.getType();
        } else {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "login";
        }
    }

    @PostMapping("/comment/{id}")
    public String comment(@PathVariable Long id, @ModelAttribute CommentDTO commentDto, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        User_info user = userRepository.findByUserId(userId).orElse(null);
        if (user != null) {
            Post post = postRepository.findById(id).orElse(null);
            if (post != null) {
                commentDto.setDate(LocalDateTime.now()); // 현재 시간을 설정
                Comment comment = commentDto.toEntity(post, user);
                comment.setWriter(user.getName());
                commentRepository.save(comment);
                return "redirect:/post/" + id;
            } else {
                model.addAttribute("message", "해당 게시글을 찾을 수 없습니다.");
                return "post/" + id;
            }
        }
        model.addAttribute("message", "로그인이 필요합니다.");
        return "login"; // 로그인 페이지로 이동
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            model.addAttribute("message", "해당 게시글을 찾을 수 없습니다.");
            return "board"; 
        }
        model.addAttribute("post", post);
        model.addAttribute("commentDto", new CommentDTO());
        return "postDetail";
    }
    
    @Transactional
    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        User_info user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        Post post = postRepository.findById(id).orElse(null);
        User_info requestUser = userRepository.findById(post.getUserId()).orElse(null);
        if (requestUser.getUserId().equals(user.getUserId())) {
            commentRepository.deleteByPostId(id);
            postRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/board";
        } else {
            redirectAttributes.addFlashAttribute("message", "다른 유저가 쓴 게시글은 삭제할 수 없습니다.");
            return "redirect:/post/" + id;
        }
    }

    @GetMapping("/deleteComment/{id}")
    public String deleteComment(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        User_info user = userRepository.findByUserId(userId).orElse(null);
        
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "login";
        }

        Comment comment = commentRepository.findById(id).orElse(null);
        
        // if (comment == null) {
        //     model.addAttribute("message", "댓글을 찾을 수 없습니다.");
        //     return "redirect:/post/" + post.getId();
        // }

        Post post = comment.getPost();
        System.out.println("user: " + user.getUserId());
        System.out.println("comment.getUser().getUserId(): " + comment.getUser().getUserId());

        if (comment.getUser().getUserId().equals(user.getUserId())) {
            commentRepository.deleteById(id);
            return "redirect:/post/" + post.getId();
        } else {
            redirectAttributes.addFlashAttribute("message", "다른 유저가 쓴 댓글은 삭제할 수 없습니다.");
            return "redirect:/post/" + post.getId();
        }
    }


    // @GetMapping("/post/{id}")
    // public String postDetail(@PathVariable Long id, Model model, HttpServletResponse response) {

    //     response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    //     response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    //     response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    //     response.setHeader("Pragma", "no-cache");

    //     Post post = postRepository.findById(id).orElse(null);
    //     model.addAttribute("post", post);
    //     return "postDetail";
    // }
    // @GetMapping("/delete/{id}")
    // public String deletePost(@PathVariable Long id, HttpServletResponse response) {

    //     response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    //     response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    //     response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    //     response.setHeader("Pragma", "no-cache");
        
    //     postRepository.deleteById(id);
    //     return "redirect:/board";
    // }

    @GetMapping("/mainMenu")
    public String mainMenu(HttpSession session, Model model, HttpServletResponse response) {
        
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            return "redirect:/login"; 
        }
        String name = user.getName();
        model.addAttribute("name", name);
        return "mainMenu";
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        System.out.println("userId dasdasdas: " + userId);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        System.out.println("user dasdassd : " + user);
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "사용자 정보를 찾을 수 없습니다.");
            return "redirect:/login";
        }

        System.out.println("세팅에 유저 정보를 전달하겠습니다.");
        model.addAttribute("userData", user);
        return "settings";
    }

    @PostMapping("/updateSettings")
    public ResponseEntity<?> updateSettings(@RequestBody User_info userData, HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String currentUserId = (String) session.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"사용자가 로그인하지 않았습니다.\"}");
        }

        User_info existingUser = userRepository.findByUserId(currentUserId).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"사용자 정보를 찾을 수 없습니다.\"}");
        }

        updateUserData(existingUser, userData);

        userRepository.save(existingUser);
        return ResponseEntity.ok("{\"message\":\"설정이 업데이트 되었습니다.\"}");
    }

    private void updateUserData(User_info existingUser, User_info newData) {
        existingUser.setSelectedMode(newData.getSelectedMode());
        existingUser.setSelectedPlaces(newData.getSelectedPlaces());
        existingUser.setSearchRadius(newData.getSearchRadius());
        existingUser.setMinDistance(newData.getMinDistance());
    }






    @GetMapping("/userDetails")
    public String userDetails(HttpSession session, Model model, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        System.out.println("User data: " + userData);

        if (userData == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        List<ScoreHistory> scoreHistoryList = userData.getScoreHistory();
        System.out.println("Score history list: " + scoreHistoryList);
        int totalPoints = scoreHistoryList.stream().mapToInt(ScoreHistory::getPoints).sum();

        System.out.println("Total points calculated: " + totalPoints);

        model.addAttribute("userData", userData);
        model.addAttribute("totalPoints", totalPoints);
        return "userDetails";
    }

    

    @GetMapping("/scoreHistory")
    public String ShowScoreHistory(HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        System.out.println("userId: " + userId);
        User_info user = userRepository.findByUserId(userId).orElse(null); // 세션에 남아있는 유저 아이디로 유저 정보 반환받음
        System.out.println("user: " + user);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<ScoreHistory> userScoreHistory = user.getScoreHistory(); // 해당 유저의 User_info 앤타티에 OneToMany로 맵핑된 ScoreHistory 객체를 가져옴
        System.out.println("Score history: " + userScoreHistory);
        
        model.addAttribute("sh", userScoreHistory); // 모델 객체를 이용해 뷰에 반환
        return "scoreHistory";
    }
    

    @PostMapping("/addScore")
    public ResponseEntity<?> addScore(@RequestBody ScoreHistoryDTO scoreDto, HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 되어 있지 않음");
        }
        
        Optional<User_info> optionalUser = userRepository.findByUserId(userId); // 세션에 남아있는 유저 어이디로 유저 정보 반환받음
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없음");
        }
        
        User_info user = optionalUser.get(); // 옵셔널 객체로 받은 유저 정보를 유저 인포 객체로 변환

        ScoreHistory newScore = new ScoreHistory(
            scoreDto.getType(), 
            scoreDto.getPoints(), 
            scoreDto.getSName(), 
            scoreDto.getEName(), 
            scoreDto.getDistance(), 
            scoreDto.getTime(), 
            user
        ); // 뷰에서 리퀘스트 바디로 전달받은 정보 세팅

        System.out.println("점수 추가 : " + newScore);

        user.getScoreHistory().add(newScore); // 세팅한 점수 기록을 유저 정보에 추가
        userRepository.saveAndFlush(user); // 세이브앤플러쉬로 즉시 적용
        
        ScoreResult scoreResult = userService.updateScore(user); // 유저 서비스 클래스에서 총 점수와 총 이동거리 정보 업데이트
        if (scoreResult == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("점수 업데이트 실패");
        }
        
        return ResponseEntity.ok(scoreResult.getTotalPoints()); // 뷰에서 fetch로 요청한 정보 반환
    }

    @GetMapping("/myProfile")
    public String showMyProfile(Model model, HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData == null) {
            return "redirect:/login";
        }
        
        int rank = userService.findUserRanking(userData.getId());
        model.addAttribute("user", userData);
        model.addAttribute("rank", rank);
        return "myProfile";
    }

    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String currentUser = (String) session.getAttribute("userId");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info userData = userRepository.findById(id).orElse(null);
        System.out.println("타인의 프로필 user: " + userData + ", id: " + id);
        if (userData == null) {
            redirectAttributes.addFlashAttribute("message", "탈퇴한 유저입니다.");
            return "redirect:/board";
        }

        int rank = userService.findUserRanking(id);
        model.addAttribute("user", userData);
        model.addAttribute("rank", rank);
        return "profile";
    }

    @GetMapping("/editProfile")
    public String showEditProfileForm(Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.asMap().get("message"));
        }

        model.addAttribute("user", userData);
        return "editProfile";
    }


    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User_info user, HttpSession session, Model model, 
    RedirectAttributes redirectAttributes, HttpServletResponse response) {
        
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info currentUser = userRepository.findByUserId(userId).orElse(null);

        if (currentUser == null) {
            model.addAttribute("message", "세션이 만료되었습니다. 다시 로그인해 주세요.");
            return "redirect:/login";
        }

        System.out.println("currentUser: " + currentUser);
        System.out.println("user: " + user);

        user.setId(currentUser.getId());
        if (userService.updateUserInfo(user, currentUser)) {
            session.setAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다!");
            return "redirect:/mainMenu";
        } else {
            model.addAttribute("message", "정보 수정에 실패했습니다.");
            return "myProfile";
        }
    }


    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session, Model model, 
    RedirectAttributes redirectAttributes, HttpServletResponse response) {
        
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        System.out.println("탈퇴 요청 유저 user: " + userData + ", id: " + id);
        if (userData != null && userData.getId() == id) {
            boolean success = userService.deleteUser(id);
            if (success) {
                session.invalidate();  
                redirectAttributes.addFlashAttribute("message", "탈퇴가 완료되었습니다. 안녕히 가세요!");
                return "redirect:/login";  
            }
        }
        model.addAttribute("message", "탈퇴에 실패했습니다.");
        return "myProfile";
    }
    

    @GetMapping("/getMyProfileImg")
    public ResponseEntity<Integer> getMyProfileImg(HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData != null) {
            return ResponseEntity.ok(userData.getProfileImage());
        } else {
            return ResponseEntity.ok(1);
        }
    }

    @PostMapping("/updateProfileImg")
    public ResponseEntity<?> updateProfileImg(@RequestBody int profileImg, HttpSession session, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData != null) {
            userData.setProfileImage(profileImg);
            userRepository.save(userData);
            return ResponseEntity.ok("프로필 이미지가 업데이트 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/rankings")
    public String showRankings(Model model, HttpSession session, RedirectAttributes redirectAttributes, HttpServletResponse response) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        System.out.println("userId: " + userId);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        List<User_info> pointUsers = userService.calculatePointsRanking();
        List<User_info> distanceUsers = userService.calculateDistanceRanking();

        System.out.println("pointUsers: " + pointUsers);
        System.out.println("distanceUsers: " + distanceUsers);

        
        

        model.addAttribute("pointUsers", pointUsers);
        model.addAttribute("distanceUsers", distanceUsers);

        List<Crew> crews = crewRepository.findAll();
        crews.sort((c1, c2) -> Double.compare(c2.getTotalPoints(), c1.getTotalPoints()));
        model.addAttribute("pointCrews", crews);
        crews.sort((c1, c2) -> Double.compare(c2.getTotalDistance(), c1.getTotalDistance()));
        model.addAttribute("distanceCrews", crews);
        return "rankings";
    }

    @GetMapping("/myCrew")
    public String showMyCrew(Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        System.out.println("userData: " + userData);
        Crew userCrew = userData.getCrew();
        System.out.println("userCrew: " + userCrew);

        int memberCount = 0;
        int totalPoints = 0;
        double totalDistance = 0;

        if (userCrew != null) {
            memberCount = userCrew.getMemberCount();
            totalPoints = userCrew.getTotalPoints();
            totalDistance = userCrew.getTotalDistance();
        } 
        
        model.addAttribute("userData", userData);
        model.addAttribute("crew", userCrew);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("totalDistance", totalDistance);
        return "myCrew";
    }

    @GetMapping("/createCrew")
    public String showCreateCrewPage(Model model, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info adminUser = userRepository.findByUserId(userId).orElse(null);
        Crew existingCrew = adminUser.getCrew();
        System.out.println("existingCrew: " + existingCrew);
        if (existingCrew != null) {
            redirectAttributes.addFlashAttribute("message", "이미 크루에 가입되어 있습니다.");
            return "redirect:/myCrew";
        }

        model.addAttribute("crew", new Crew()); 
        return "createCrew";
    }

    @PostMapping("/createCrew")
    public String createCrew(@ModelAttribute CrewDTO dto, Model model, HttpServletResponse response, 
        HttpSession session, RedirectAttributes redirectAttributes) {
        
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        
        User_info adminUser = userRepository.findByUserId(userId).orElse(null);

        if (crewRepository.existsByName(dto.getName())) {
            model.addAttribute("message", "이미 존재하는 크루 이름입니다.");
            model.addAttribute("crew", dto);
            return "createCrew";
        }

        Crew crew = dto.toEntity(adminUser);
        crew.getMembers().add(adminUser);
        adminUser.setCrew(crew);  // 양방향 관계 설정
        crewRepository.save(crew);  // crew 저장
        userRepository.save(adminUser);  // user 정보 업데이트

        redirectAttributes.addFlashAttribute("message", "크루 생성이 완료되었습니다!");
        return "redirect:/myCrew";
    }


    @GetMapping("/crewProfile/{id}")
    public String showCrewProfile(@PathVariable Long id, Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String currentUser = (String) session.getAttribute("userId");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Crew crewData = crewRepository.findById(id).orElse(null);
        System.out.println("크루 프로필 : " + crewData + ", id: " + id);
        if (crewData == null) {
            redirectAttributes.addFlashAttribute("message", "해체된 크루입니다.");
            return "redirect:/mainMenu";
        }

        List<Crew> crews = crewRepository.findAll();
        crews.sort((c1, c2) -> Double.compare(c2.getTotalPoints() + c2.getTotalDistance(), c1.getTotalPoints() + c1.getTotalDistance()));

        int rank = 0;
        for (int i = 0; i < crews.size(); i++) {
            if (crews.get(i).getId().equals(id)) {
                rank = i + 1;
            }
        }

        int memberCount = crewData.getMemberCount();
        int totalPoints = crewData.getTotalPoints();
        double totalDistance = crewData.getTotalDistance();

        model.addAttribute("crew", crewData);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("totalDistance", totalDistance);
        model.addAttribute("rank", rank);
        return "crewProfile";
    }

    @GetMapping("/editCrewProfile")
    public String showEditCrewProfileForm(Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Crew crewData = userData.getCrew();
        if (crewData == null) {
            redirectAttributes.addFlashAttribute("message", "크루에 가입되어 있지 않습니다.");
            return "redirect:/mainMenu";
        }

        if (crewData.getAdmin().getId() != userData.getId()) {
            redirectAttributes.addFlashAttribute("message", "크루 관리자만 수정 가능합니다.");
            return "redirect:/myCrew";
        }

        model.addAttribute("crew", crewData);
        return "editCrewProfile";
    }

    @PostMapping("/updateCrewProfile")
    public String updateCrewProfile(@ModelAttribute CrewDTO dto, HttpSession session, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);
        if (userData == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "login";
        }

        Crew crewData = userData.getCrew();
        if (crewData == null) {
            model.addAttribute("message", "크루에 가입되어 있지 않습니다.");
            return "myCrew";
        }

        if (!crewData.getAdmin().getId().equals(userData.getId())) {
            model.addAttribute("message", "크루 관리자만 수정 가능합니다.");
            return "myCrew";
        }

        if (!crewData.getName().equals(dto.getName()) && crewRepository.existsByName(dto.getName())) {
            model.addAttribute("message", "이미 존재하는 크루 이름입니다.");
            model.addAttribute("crew", dto);
            return "editCrewProfile";
        }

        if (crewData.getMemberCount() > dto.getCapacity()) {
            model.addAttribute("message", "수용 인원을 현재 인원보다 작게 설정할 수 없습니다.");
            model.addAttribute("crew", dto);
            return "editCrewProfile";
        }

        crewData.setName(dto.getName());
        crewData.setCapacity(dto.getCapacity());
        crewData.setRegion(dto.getRegion());
        crewData.setProfileImage(dto.getProfileImage());
        crewRepository.save(crewData);

        redirectAttributes.addFlashAttribute("message", "크루 정보가 수정되었습니다.");
        return "redirect:/myCrew";
    }

    @GetMapping("/expelMember/{memberId}")
    public ResponseEntity<Map<String, Object>> expelMember(@PathVariable Long memberId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userId = (String) session.getAttribute("userId");
        User_info userData = userRepository.findByUserId(userId).orElse(null);

        if (userData == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Crew crewData = userData.getCrew();
        if (crewData == null) {
            response.put("success", false);
            response.put("message", "크루에 가입되어 있지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!crewData.getAdmin().getId().equals(userData.getId())) {
            response.put("success", false);
            response.put("message", "크루 관리자만 멤버를 추방할 수 있습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        User_info memberToExpel = userRepository.findById(memberId).orElse(null);
        if (memberToExpel == null || !crewData.getMembers().contains(memberToExpel)) {
            response.put("success", false);
            response.put("message", "추방할 멤버를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        crewData.getMembers().remove(memberToExpel);
        memberToExpel.setCrew(null); // User_info 엔티티의 crew 필드를 null로 설정
        userRepository.save(memberToExpel); // 변경 사항 저장
        crewRepository.save(crewData);

        response.put("success", true);
        response.put("message", "멤버가 추방되었습니다.");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/md")
    public String makeDummyData(RedirectAttributes redirectAttributes) {
        Random random = new Random();

        for (int i = 3; i < 11; i++) {
            User_info user = new User_info();

            String userId = String.valueOf(i);
            String userId2 = String.valueOf(random.nextInt(100) + 10); 
            user.setName(userId2);
            user.setUserId(userId);
            user.setPw(userId);
            user.setEmail(userId+"@test.com");
            int randomNumber = random.nextInt(5) + 1;
            user.setProfileImage(randomNumber);

            System.out.println("user: " + user);
            userRepository.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "더미 데이터를 랜덤으로 생성하였습니다.");
        return "redirect:/login";
    }
    
    
}


