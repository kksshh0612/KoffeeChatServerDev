package teamkiim.koffeechat.vote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.repository.PostRepository;
import teamkiim.koffeechat.vote.service.dto.request.ModifyVoteServiceRequest;
import teamkiim.koffeechat.vote.service.dto.request.SaveVoteServiceRequest;
import teamkiim.koffeechat.vote.controller.dto.request.SaveVoteRecordRequest;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.domain.VoteItem;
import teamkiim.koffeechat.vote.domain.VoteRecord;
import teamkiim.koffeechat.vote.repository.VoteItemRepository;
import teamkiim.koffeechat.vote.repository.VoteRecordRepository;
import teamkiim.koffeechat.vote.repository.VoteRepository;
import teamkiim.koffeechat.vote.service.dto.SaveVoteRecordServiceDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteRecordRepository voteRecordRepository;

    //멤버가 투표를 했는지
    public boolean hasMemberVoted(Long voteId, Long memberId) {
        return voteRecordRepository.findByVoteIdAndMemberId(voteId, memberId).isPresent();
    }


    /**
     * 투표 저장
     *
     * @param saveVoteServiceRequest 투표 저장 dto
     * @param postId                 연관된 게시물 pk
     * @return Vote
     */
    @Transactional
    public Vote saveVote(SaveVoteServiceRequest saveVoteServiceRequest, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isEditing()) {  // 작성 중이 아니라면 투표를 생성할 수 없다.
            throw new CustomException(ErrorCode.VOTE_FORBIDDEN);
        }

        Vote vote = saveVoteServiceRequest.toEntity(post, saveVoteServiceRequest.getTitle());  //투표 생성
        Vote saveVote = voteRepository.save(vote);

        for (VoteItem voteItem : vote.getVoteItems()) {
            voteItemRepository.save(voteItem);
        }

        post.addVote(saveVote);                      //양방향 연관관계 주입

        return vote;
    }

    /**
     * 투표 내용 수정
     *
     * @param modifyVoteServiceRequest 투표 수정 요청
     * @param vote                     수정할 투표
     */
    @Transactional
    public void modifyVote(ModifyVoteServiceRequest modifyVoteServiceRequest, Vote vote) {

        vote.modify(modifyVoteServiceRequest.getTitle());

        List<VoteItem> voteItemList = voteItemRepository.findByVote(vote);
        List<String> newVoteItemList = modifyVoteServiceRequest.getItems();

        //투표 항목 추가
        if (newVoteItemList.size() > voteItemList.size()) {
            for (int i = voteItemList.size() ; i < newVoteItemList.size(); i++) {
                VoteItem voteItem = new VoteItem(vote, newVoteItemList.get(i));
                VoteItem savedVoteItem=voteItemRepository.save(voteItem);
                vote.addVoteItem(savedVoteItem);
            }
        }
    }

    /**
     * 투표
     *
     * @param postId                투표 항목과 연관된 게시물의 PK
     * @param saveVoteRecordRequest 투표 요청 dto
     * @param memberId              투표한 멤버
     * @return isVoted 필드를 포함한 dto
     */
    @Transactional
    public ResponseEntity<?> saveVoteRecord(Long postId, SaveVoteRecordRequest saveVoteRecordRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Long> voteItems = saveVoteRecordRequest.getItems();  //투표한 항목들
        List<VoteItem> voteItemList = new ArrayList<>();

        for (Long voteItemId : voteItems) {
            VoteItem voteItem = voteItemRepository.findById(voteItemId)
                    .orElseThrow(() -> new CustomException(ErrorCode.VOTE_ITEM_NOT_FOUND));  //findAllIn
            voteItemList.add(voteItem);
        }

        Vote vote = voteItemRepository.findVoteByVoteItemId(voteItems.get(0))
                .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

        if (!hasMemberVoted(vote.getId(), memberId)) {  //처음 투표
            //로그인한 멤버(투표한 멤버)의 투표 기록 저장
            for (VoteItem voteItem : voteItemList) {
                VoteRecord voteRecord = VoteRecord.create(member, voteItem);
                VoteRecord saveVoteRecord = voteRecordRepository.save(voteRecord);
                voteItem.addVoteRecord(saveVoteRecord);  //연관관계 주입
                voteItem.addVoteCount();                 //투표 수 ++
            }
        }

        List<SaveVoteRecordServiceDto> saveVoteRecordServiceDto = vote.getVoteItems().stream()
                .map(SaveVoteRecordServiceDto::of).collect(Collectors.toList());

        return ResponseEntity.ok(saveVoteRecordServiceDto);
    }


    /**
     * 투표 삭제
     */

}

