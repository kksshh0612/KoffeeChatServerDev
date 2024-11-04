package teamkiim.koffeechat.domain.chat.message.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import teamkiim.koffeechat.domain.chat.message.domain.AutoIncrementSequence;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public Long generateSequence(String seqName) {

        AutoIncrementSequence counter = mongoOperations.findAndModify(
                Query.query(where("seqId").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                AutoIncrementSequence.class);

        return counter != null ? counter.getSeq() : 1;
    }
}
