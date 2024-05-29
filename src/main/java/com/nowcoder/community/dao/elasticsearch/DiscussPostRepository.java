package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
/**
 * ClassName: DiscussPostRepository
 * Package: com.nowcoder.community.dao.elasticsearch
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {



}
