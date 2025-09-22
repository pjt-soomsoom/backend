-- 1. 'items' 테이블의 'name' 컬럼에 unique 제약 조건을 추가합니다.
--    이 제약 조건은 모든 아이템이 고유한 이름을 갖도록 보장합니다.
ALTER TABLE items
    ADD CONSTRAINT uk_items_name UNIQUE (name);

-- 2. 'collections' 테이블의 'name' 컬럼에 unique 제약 조건을 추가합니다.
--    이 제약 조건은 모든 컬렉션이 고유한 이름을 갖도록 보장합니다.
ALTER TABLE collections
    ADD CONSTRAINT uk_collections_name UNIQUE (name);

-- 3. 'collection_items' 조인 테이블에 복합 unique 제약 조건을 추가합니다.
--    이 제약 조건은 하나의 컬렉션에 동일한 아이템이 중복으로 추가되는 것을 방지합니다.
ALTER TABLE collection_items
    ADD CONSTRAINT uk_collection_items UNIQUE (collection_id, item_id);
