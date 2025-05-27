package id.ac.ui.cs.advprog.udehnihpayment.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    @Test
    public void testBankValues_ShouldHaveCorrectCount() {
        Bank[] banks = Bank.values();
        assertEquals(5, banks.length);
    }

    @Test
    public void testBCA_ShouldHaveCorrectProperties() {
        Bank bca = Bank.BCA;
        assertEquals("Bank Central Asia", bca.getBankName());
        assertEquals("123-456-7890", bca.getAccountNumber());
        assertEquals("Udehnih", bca.getAccountName());
    }

    @Test
    public void testBNI_ShouldHaveCorrectProperties() {
        Bank bni = Bank.BNI;
        assertEquals("Bank Negara Indonesia", bni.getBankName());
        assertEquals("987-654-3210", bni.getAccountNumber());
        assertEquals("Udehnih", bni.getAccountName());
    }

    @Test
    public void testMANDIRI_ShouldHaveCorrectProperties() {
        Bank mandiri = Bank.MANDIRI;
        assertEquals("Bank Mandiri", mandiri.getBankName());
        assertEquals("456-789-0123", mandiri.getAccountNumber());
        assertEquals("Udehnih", mandiri.getAccountName());
    }

    @Test
    public void testBRI_ShouldHaveCorrectProperties() {
        Bank bri = Bank.BRI;
        assertEquals("Bank Rakyat Indonesia", bri.getBankName());
        assertEquals("789-012-3456", bri.getAccountNumber());
        assertEquals("Udehnih", bri.getAccountName());
    }

    @Test
    public void testCIMB_ShouldHaveCorrectProperties() {
        Bank cimb = Bank.CIMB;
        assertEquals("CIMB Niaga", cimb.getBankName());
        assertEquals("998-877-6655", cimb.getAccountNumber());
        assertEquals("PT Udehnih", cimb.getAccountName());
    }

    @Test
    public void testBankValueOf_ValidBank_ShouldReturnCorrectEnum() {
        assertEquals(Bank.BCA, Bank.valueOf("BCA"));
        assertEquals(Bank.BNI, Bank.valueOf("BNI"));
        assertEquals(Bank.MANDIRI, Bank.valueOf("MANDIRI"));
        assertEquals(Bank.BRI, Bank.valueOf("BRI"));
        assertEquals(Bank.CIMB, Bank.valueOf("CIMB"));
    }

    @Test
    public void testBankValueOf_InvalidBank_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Bank.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> Bank.valueOf(""));
        assertThrows(IllegalArgumentException.class, () -> Bank.valueOf("bca"));
        assertThrows(IllegalArgumentException.class, () -> Bank.valueOf("Bank Central Asia"));
    }

    @Test
    public void testBankValueOf_NullInput_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> Bank.valueOf(null));
    }

    @Test
    public void testBankToString_ShouldReturnEnumName() {
        assertEquals("BCA", Bank.BCA.toString());
        assertEquals("BNI", Bank.BNI.toString());
        assertEquals("MANDIRI", Bank.MANDIRI.toString());
        assertEquals("BRI", Bank.BRI.toString());
        assertEquals("CIMB", Bank.CIMB.toString());
    }

    @Test
    public void testBankEquality_SameBank_ShouldBeEqual() {
        Bank bca1 = Bank.BCA;
        Bank bca2 = Bank.valueOf("BCA");
        assertEquals(bca1, bca2);
        assertSame(bca1, bca2);
    }

    @Test
    public void testBankInequality_DifferentBanks_ShouldNotBeEqual() {
        assertNotEquals(Bank.BCA, Bank.BNI);
        assertNotEquals(Bank.MANDIRI, Bank.BRI);
        assertNotEquals(Bank.CIMB, Bank.BCA);
    }

    @Test
    public void testAllBanksHaveUniqueProperties() {
        Bank[] banks = Bank.values();
        
        // Test unique bank names
        for (int i = 0; i < banks.length; i++) {
            for (int j = i + 1; j < banks.length; j++) {
                assertNotEquals(banks[i].getBankName(), banks[j].getBankName(),
                    "Bank names should be unique: " + banks[i] + " vs " + banks[j]);
            }
        }
        
        // Test unique account numbers
        for (int i = 0; i < banks.length; i++) {
            for (int j = i + 1; j < banks.length; j++) {
                assertNotEquals(banks[i].getAccountNumber(), banks[j].getAccountNumber(),
                    "Account numbers should be unique: " + banks[i] + " vs " + banks[j]);
            }
        }
    }

    @Test
    public void testBankAccountNameConsistency() {
        // Most banks should have "Udehnih" as account name, except CIMB
        assertEquals("Udehnih", Bank.BCA.getAccountName());
        assertEquals("Udehnih", Bank.BNI.getAccountName());
        assertEquals("Udehnih", Bank.MANDIRI.getAccountName());
        assertEquals("Udehnih", Bank.BRI.getAccountName());
        assertEquals("PT Udehnih", Bank.CIMB.getAccountName());
    }

    @Test
    public void testBankPropertiesNotNull() {
        for (Bank bank : Bank.values()) {
            assertNotNull(bank.getBankName(), "Bank name should not be null for " + bank);
            assertNotNull(bank.getAccountNumber(), "Account number should not be null for " + bank);
            assertNotNull(bank.getAccountName(), "Account name should not be null for " + bank);
        }
    }

    @Test
    public void testBankPropertiesNotEmpty() {
        for (Bank bank : Bank.values()) {
            assertFalse(bank.getBankName().isEmpty(), "Bank name should not be empty for " + bank);
            assertFalse(bank.getAccountNumber().isEmpty(), "Account number should not be empty for " + bank);
            assertFalse(bank.getAccountName().isEmpty(), "Account name should not be empty for " + bank);
        }
    }

    @Test
    public void testBankOrdinalValues() {
        assertEquals(0, Bank.BCA.ordinal());
        assertEquals(1, Bank.BNI.ordinal());
        assertEquals(2, Bank.MANDIRI.ordinal());
        assertEquals(3, Bank.BRI.ordinal());
        assertEquals(4, Bank.CIMB.ordinal());
    }

    @Test
    public void testBankHashCodeConsistency() {
        Bank bca1 = Bank.BCA;
        Bank bca2 = Bank.valueOf("BCA");
        assertEquals(bca1.hashCode(), bca2.hashCode());
    }

    @Test
    public void testBankAccountNumberFormat() {
        // Test that account numbers follow expected pattern (XXX-XXX-XXXX)
        for (Bank bank : Bank.values()) {
            String accountNumber = bank.getAccountNumber();
            assertTrue(accountNumber.matches("\\d{3}-\\d{3}-\\d{4}"), 
                "Account number should follow XXX-XXX-XXXX pattern for " + bank + " but was: " + accountNumber);
        }
    }
}
