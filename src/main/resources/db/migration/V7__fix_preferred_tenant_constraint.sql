ALTER TABLE properties DROP CONSTRAINT IF EXISTS properties_preferred_tenant_check;
ALTER TABLE properties ADD CONSTRAINT properties_preferred_tenant_check
    CHECK (preferred_tenant IN ('BACHELOR', 'FAMILY', 'BOTH'));

UPDATE properties SET preferred_tenant = UPPER(preferred_tenant)
WHERE preferred_tenant IN ('bachelor', 'family', 'both');
