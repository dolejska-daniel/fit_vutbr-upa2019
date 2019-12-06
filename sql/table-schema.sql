--=========================================================================dd==
--  TABLE DELETION
--=========================================================================dd==
DROP TABLE entries;
DROP TABLE images;
DROP TABLE geometry;

--=========================================================================dd==
--  TABLE CREATION
--=========================================================================dd==
CREATE TABLE entries
(
    id          NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    name        VARCHAR2(64),
    description CLOB
);

CREATE TABLE images
(
    id          NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    entry_id    NUMBER(5),
    title       VARCHAR2(64),
    description CLOB,
    image       ORDSYS.ORDImage,
    image_si    ORDSYS.SI_StillImage,
    image_ac    ORDSYS.SI_AverageColor,
    image_ch    ORDSYS.SI_ColorHistogram,
    image_pc    ORDSYS.SI_PositionalColor,
    image_tx    ORDSYS.SI_Texture,

    -- Foreign key constraints
    CONSTRAINT FK_images_entry
        FOREIGN KEY (entry_id)
            REFERENCES entries (id)
);

CREATE TABLE geometry
(
    id       NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    entry_id NUMBER(5),
    type     VARCHAR2(16),
    data     SDO_GEOMETRY,

    -- Foreign key constraints
    CONSTRAINT FK_geometry_entry
        FOREIGN KEY (entry_id)
            REFERENCES entries (id)
);
