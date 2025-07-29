package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CreateDocumentRequest;
import com.swp391.eschoolmed.dto.request.UpdateDocumentRequest;
import com.swp391.eschoolmed.dto.response.DocumentResponse;
import com.swp391.eschoolmed.model.Document;
import com.swp391.eschoolmed.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // tạo bài viết
    @PostMapping("/create_document")
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    public ApiResponse<String> createBlog(@RequestBody CreateDocumentRequest request,
                                          @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        Document doc = new Document();
        doc.setUserId(userId);
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        documentService.create(doc);
        return ApiResponse.<String>builder()
                .message("Tạo bài viết thành công.")
                .result("OK")
                .build();
    }

    // hiển thị bài biết
    @GetMapping("/get_all_blog")
    public ApiResponse<List<DocumentResponse>> getAllBlogs() {
        List<DocumentResponse> result = documentService.getAll().stream()
                .map(documentService::toResponse)
                .toList();
        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Lấy danh sách bài viết thành công.")
                .result(result)
                .build();
    }

    // lấy 1 bài viết
    @GetMapping("/get/{id}")
    public ApiResponse<DocumentResponse> getBlogById(@PathVariable Long id) {
        Document doc = documentService.getById(id);
        return ApiResponse.<DocumentResponse>builder()
                .message("Lấy bài viết thành công.")
                .result(documentService.toResponse(doc))
                .build();
    }

    // UPDATE
    @PutMapping("/update_document/{id}")
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    public ApiResponse<String> updateBlog(@PathVariable Long id,
                                          @RequestBody UpdateDocumentRequest request,
                                          @AuthenticationPrincipal Jwt jwt) throws AccessDeniedException {
        Long userId = Long.parseLong(jwt.getSubject());
        documentService.updateIfOwner(id, request, userId);
        return ApiResponse.<String>builder()
                .message("Cập nhật bài viết thành công.")
                .result("OK")
                .build();
    }

    // DELETE
    @DeleteMapping("/delete_document/{id}")
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    public ApiResponse<String> deleteBlog(@PathVariable Long id,
                                          @AuthenticationPrincipal Jwt jwt) throws AccessDeniedException {
        Long userId = Long.parseLong(jwt.getSubject());
        documentService.deleteIfOwner(id, userId);
        return ApiResponse.<String>builder()
                .message("Xóa bài viết thành công.")
                .result("OK")
                .build();
    }

    @GetMapping("/my-documents")
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    public ApiResponse<List<DocumentResponse>> getMyDocuments(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());

        List<DocumentResponse> docs = documentService.getAll().stream()
                .filter(doc -> doc.getUserId().equals(userId))
                .map(documentService::toResponse)
                .toList();

        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Lấy bài viết của bạn thành công.")
                .result(docs)
                .build();
    }

}
