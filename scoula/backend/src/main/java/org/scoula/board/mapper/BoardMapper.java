package org.scoula.board.mapper;

import org.apache.ibatis.annotations.Select;
import org.scoula.board.domain.BoardAttachmentVO;
import org.scoula.board.domain.BoardVO;
import org.scoula.common.pagination.PageRequest;

import java.util.List;

public interface BoardMapper {
//게시글 전체 개수 변환
    int getTotalCount();
//페이지 요펑 정보 받아서 해당 페이지에 맞는 데이터만 가져옴
    List<BoardVO> getPage(PageRequest pageRequest);

    //    @Select("select * from tbl_board order by no desc")
    public List<BoardVO> getList();

    public BoardVO get(Long no);

    public void create(BoardVO board);

    public int update(BoardVO board);

    public int delete(Long no);

    public void createAttachment(BoardAttachmentVO attach);

    public List<BoardAttachmentVO> getAttachmentList(Long bno);

    public BoardAttachmentVO getAttachment(Long no);

    public int deleteAttachment(Long no);


}
