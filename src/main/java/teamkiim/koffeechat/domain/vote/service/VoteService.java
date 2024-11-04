package teamkiim.koffeechat.domain.vote.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.domain.vote.domain.Vote;
import teamkiim.koffeechat.domain.vote.domain.VoteItem;
import teamkiim.koffeechat.domain.vote.domain.VoteRecord;
import teamkiim.koffeechat.domain.vote.dto.SaveVoteRecordServiceDto;
import teamkiim.koffeechat.domain.vote.dto.request.ModifyVoteServiceRequest;
import teamkiim.koffeechat.domain.vote.dto.request.SaveVoteServiceRequest;
import teamkiim.koffeechat.domain.vote.repository.VoteItemRepository;
import teamkiim.koffeechat.domain.vote.repository.VoteRecordRepository;
import teamkiim.koffeechat.domain.vote.repository.VoteRepository;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteRecordRepository voteRecordRepository;

    private final AESCipherUtil aesCipherUtil;

    //멤버가 투표를 했는지 안했는지
    public boolean hasMemberVoted(Vote vote, Member member) {
        return !voteRecordRepository.findByVoteAndMember(vote, member).isEmpty();
    }

    /**
     * 투표 저장
     *
     * @param saveVoteServiceRequest 투표 저장 dto
     * @param postId                 연관된 게시물 pk
     * @return Vote
     */
    @Transactional
    public void saveVote(SaveVoteServiceRequest saveVoteServiceRequest, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성 중이 아니라면 투표를 생성할 수 없다.
        if (post.isEditing()) {
            throw new CustomException(ErrorCode.VOTE_FORBIDDEN);
        }

        Vote vote = saveVoteServiceRequest.toEntity(post, saveVoteServiceRequest.getTitle());  //투표 생성
        voteRepository.save(vote);
        voteItemRepository.saveAll(vote.getVoteItems());
    }

    /**
     * 투표 내용 수정 : 투표 항목 추가만 가능
     *
     * @param modifyVoteServiceRequest 투표 수정 요청
     * @param vote                     수정할 투표
     */
    @Transactional
    public void modifyVote(ModifyVoteServiceRequest modifyVoteServiceRequest, Vote vote) {

        if (modifyVoteServiceRequest == null) {
            return;
        }
        if (modifyVoteServiceRequest.getItems() == null) {
            throw new CustomException(ErrorCode.INVALID_VOTE_REQUEST);
        }

        List<VoteItem> voteItemList = voteItemRepository.findByVote(vote);
        List<String> newVoteItemList = modifyVoteServiceRequest.getItems();

        //투표 항목 추가
        if (newVoteItemList.size() > voteItemList.size()) {
            for (int i = voteItemList.size(); i < newVoteItemList.size(); i++) {
                VoteItem savedVoteItem = voteItemRepository.save(new VoteItem(vote, newVoteItemList.get(i)));
                vote.addVoteItem(savedVoteItem);
            }
        }
    }

    /**
     * 투표 / 재투표
     *
     * @param postId                투표 항목과 연관된 게시물의 PK
     * @param saveVoteRecordRequest 투표 요청 dto
     * @param memberId              투표한 멤버
     * @return isVoted 필드를 포함한 dto
     */
    @Transactional
    public List<SaveVoteRecordServiceDto> saveVoteRecord(Long postId, List<Long> saveVoteRecordRequest,
                                                         Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Vote vote = voteRepository.findByPost(post)
                .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

        List<VoteItem> voteItemList = voteItemRepository.findAllByPostAndIds(post,
                saveVoteRecordRequest);  //투표한 항목 존재 여부 확인

        if (!saveVoteRecordRequest.isEmpty() && voteItemList.isEmpty()) {
            throw new CustomException(ErrorCode.VOTE_ITEM_NOT_FOUND);
        }

        List<VoteRecord> voteRecordList = voteRecordRepository.findByVoteAndMember(vote, member);  //기존 투표 기록
        if (!voteRecordList.isEmpty()) { //기존 투표 기록 모두 삭제
            for (VoteRecord voteRecord : voteRecordList) {
                VoteItem voteItem = voteRecord.getVoteItem();
                voteItem.removeVoteRecord(voteRecord);      // 연관관계 제거
                voteItem.removeVoteCount();                 // 투표 수 --
            }
            voteRecordRepository.deleteAll(voteRecordList);
        }

        //재투표 항목들에 대해 투표 기록 생성
        if (!saveVoteRecordRequest.isEmpty()) {
            for (VoteItem votedItem : voteItemList) {
                VoteRecord saveVoteRecord = voteRecordRepository.save(VoteRecord.create(member, votedItem));
                votedItem.addVoteRecord(saveVoteRecord);  //연관관계 주입
                votedItem.addVoteCount();                 //투표 수 ++
            }
        }

        return vote.getVoteItems().stream()
                .map(voteItem -> SaveVoteRecordServiceDto.of(aesCipherUtil.encrypt(voteItem.getId()), voteItem))
                .toList();
    }

}

