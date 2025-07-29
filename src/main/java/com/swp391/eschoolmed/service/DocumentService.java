package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateDocumentRequest;
import com.swp391.eschoolmed.dto.request.UpdateDocumentRequest;
import com.swp391.eschoolmed.dto.response.DocumentResponse;
import com.swp391.eschoolmed.model.Document;
import com.swp391.eschoolmed.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public void createDocument(CreateDocumentRequest request, Long userId) {
        Document doc = new Document();
        doc.setUserId(userId);
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        documentRepository.save(doc);
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return toResponse(doc);
    }

    public void updateDocument(Long id, UpdateDocumentRequest request, Long userId) throws AccessDeniedException {
        Document doc = getDocumentEntity(id);
        if (!doc.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa bài viết này.");
        }
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        documentRepository.save(doc);
    }

    public void deleteDocument(Long id, Long userId) throws AccessDeniedException {
        Document doc = getDocumentEntity(id);
        if (!doc.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa bài viết này.");
        }
        documentRepository.delete(doc);
    }

    public List<DocumentResponse> getDocumentsByUser(Long userId) {
        return documentRepository.findAll().stream()
                .filter(doc -> doc.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Document getDocumentEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
    }

    private DocumentResponse toResponse(Document doc) {
        return DocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .userId(doc.getUserId())
                .title(doc.getTitle())
                .content(doc.getContent())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}

