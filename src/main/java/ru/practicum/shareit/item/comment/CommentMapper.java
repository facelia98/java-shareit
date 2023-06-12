package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder().id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();

    }

    public static Comment toComment(CommentDto commentDto, User author, LocalDateTime created) {
        return Comment.builder()
                .id(commentDto.getId())
                .author(author)
                .created(created)
                .text(commentDto.getText())
                .build();
    }
}
